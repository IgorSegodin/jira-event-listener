package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author isegodin
 */
@Data
@Builder
public class IssueDto {

    private String key;
    private Long number;
    private Long projectId;
    private String summary;
    private UserDto assignee;
    private IssueConstantDto type;
    private IssueConstantDto priority;
    private IssueConstantDto status;
}
