package org.isegodin.jira.event.listener;

import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.module.SimpleModule;
import org.isegodin.jira.event.listener.data.converter.IssueEventToEventDtoConverter;
import org.isegodin.jira.event.listener.rabbit.RabbitConfig;
import org.isegodin.jira.event.listener.rabbit.RabbitMessageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.zip.GZIPOutputStream;

/**
 * Sends all JIRA issue related events to RabbitMQ topic.
 *
 * @author isegodin
 */
@Slf4j
public class RedirectToRabbitJiraEventListener extends AbstractIssueEventListener {

    private static final String RABBIT_HOST = "rabbit.host";
    private static final String RABBIT_PORT = "rabbit.port";
    private static final String RABBIT_USERNAME = "rabbit.username";
    private static final String RABBIT_PASSWORD = "rabbit.password";
    private static final String RABBIT_TOPIC = "rabbit.topic";
    private static final String GZIP_ENABLE = "gzip.enable";

    private static final String JIRA_TOPIC_ROUTING_KEY = "jira.event";

    private volatile RabbitConfig config;
    private volatile RabbitMessageService messageService;

    private volatile boolean initialized = false;
    private volatile boolean useGzip = false;

    private final IssueEventToEventDtoConverter eventConverter = new IssueEventToEventDtoConverter();

    private final ObjectMapper objectMapper = createObjectMapper();

    @Override
    public String getDescription() {
        return "Redirects issue events to RabbitMQ topic.";
    }

    @Override
    public String[] getAcceptedParams() {
        return new String[]{
                RABBIT_HOST,
                RABBIT_PORT,
                RABBIT_USERNAME,
                RABBIT_PASSWORD,
                RABBIT_TOPIC,
                GZIP_ENABLE
        };
    }

    @Override
    @SneakyThrows
    public void init(Map map) {
        synchronized (this) {
            this.initialized = false;
        }

        Map<String, String> params = map;
        RabbitConfig newConfig = RabbitConfig.builder()
                .host(getRequiredParam(RABBIT_HOST, params))
                .port(
                        Optional.ofNullable(params.get(RABBIT_PORT)).map((p -> {
                            try {
                                return Integer.parseInt(p);
                            } catch (NumberFormatException e) {
                                throw new RuntimeException("Incorrect port format " + p);
                            }
                        })).orElse(5672)
                )
                .username(getRequiredParam(RABBIT_USERNAME, params))
                .password(getRequiredParam(RABBIT_PASSWORD, params))
                .topicName(getRequiredParam(RABBIT_TOPIC, params))
                .build();

        RabbitMessageService newMessageService = null;
        try {
            newMessageService = new RabbitMessageService(newConfig);
        } catch (Exception e) {
            log.warn("Can not connect to RabbitMQ " + newConfig);
            throw e;
        }

        synchronized (this) {
            this.useGzip = Boolean.valueOf(params.get(GZIP_ENABLE));
            this.config = newConfig;
            this.messageService = newMessageService;
            this.initialized = true;
        }
    }

    private String getRequiredParam(String name, Map<String, String> params) {
        return Optional.ofNullable(params.get(name)).orElseThrow(() -> new RuntimeException("Empty parameter " + name));
    }

    @Override
    @SneakyThrows
    protected void handleDefaultIssueEvent(IssueEvent event) {
        if (!this.initialized) {
            return;
        }

        byte[] data = objectMapper.writeValueAsBytes(eventConverter.convert(event));
        if (useGzip) {
            data = compressWithGzip(data);
        }

        try {
            messageService.sendData(JIRA_TOPIC_ROUTING_KEY, data);
        } catch (Exception e) {
            log.info("Can not send message to rabbitmq topic, try to reconnect " + config, e);

            boolean reconnected = false;

            synchronized (this) {
                try {
                    messageService.sendData(JIRA_TOPIC_ROUTING_KEY, data);
                } catch (Exception e1) {
                    try {
                        RabbitMessageService newMessageService = new RabbitMessageService(config);
                        messageService.close();
                        this.messageService = newMessageService;
                        reconnected = true;
                    } catch (Exception e2) {
                        log.warn("Reconnect failed " + config, e2);
                    }
                }
            }

            if (reconnected) {
                try {
                    messageService.sendData(JIRA_TOPIC_ROUTING_KEY, data);
                } catch (Exception e1) {
                    log.warn("Can not send message to rabbitmq topic after reconnect " + config, e);
                }
            }
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        SimpleModule module = new SimpleModule("java-8-date-time", Version.unknownVersion());
        module.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
            @Override
            public void serialize(OffsetDateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            }
        });
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @SneakyThrows
    private byte[] compressWithGzip(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data);
        gzip.close();
        return out.toByteArray();
    }

}
