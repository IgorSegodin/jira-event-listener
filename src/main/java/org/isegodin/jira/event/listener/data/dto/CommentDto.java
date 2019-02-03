package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author isegodin
 */
@Data
@Builder
public class CommentDto {

    private Long id;
    private String body;
    private OffsetDateTime created;
    private UserDto author;
}
