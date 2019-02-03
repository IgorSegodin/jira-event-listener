package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author isegodin
 */
@Data
@Builder
public class EventDto {

    private UserDto user;
    private IssueDto issue;
    private WorklogDto worklog;
    private CommentDto comment;
    private OffsetDateTime timestamp;
}
