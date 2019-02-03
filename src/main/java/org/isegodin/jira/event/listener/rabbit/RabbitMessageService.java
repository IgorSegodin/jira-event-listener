package org.isegodin.jira.event.listener.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Sends messages to RabbitMQ topic.
 *
 * @author isegodin
 */
@Slf4j
public class RabbitMessageService implements AutoCloseable {

    private final RabbitConfig config;
    private final Connection connection;
    private final Channel channel;

    @SneakyThrows
    public RabbitMessageService(RabbitConfig config) {
        this.config = config;

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.getHost());
        connectionFactory.setPort(config.getPort());
        connectionFactory.setUsername(config.getUsername());
        connectionFactory.setPassword(config.getPassword());

        this.connection = connectionFactory.newConnection();
        this.channel = connection.createChannel();

        channel.exchangeDeclare(config.getTopicName(), "topic");

        log.debug("Connected to rabbitmq " + config);
    }

    @SneakyThrows
    public void sendData(String routingKey, byte[] data) {
        if (!this.connection.isOpen()) {
            throw new RuntimeException("Connection is closed " + config.toString());
        }
        channel.basicPublish(config.getTopicName(), routingKey, null, data);
    }

    @Override
    public synchronized void close() {
        try {
            this.connection.close();
        } catch (IOException e) {
            log.error("Can not close rabbitmq connection " + config, e);
        }
    }
}
