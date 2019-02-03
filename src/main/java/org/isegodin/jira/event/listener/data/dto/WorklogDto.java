package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author isegodin
 */
@Data
@Builder
public class WorklogDto {

    private Long id;
    private String comment;
    private OffsetDateTime startDate;
    private Long timeSpent;
}
