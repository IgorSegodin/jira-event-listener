package org.isegodin.jira.event.listener.rabbit;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Configuration for {@link RabbitMessageService}
 *
 * @author isegodin
 */
@Data
@Builder
@ToString(exclude = {"password"})
public class RabbitConfig {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String topicName;

}
