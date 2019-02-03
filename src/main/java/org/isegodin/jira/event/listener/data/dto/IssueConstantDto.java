package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author isegodin
 */
@Data
@Builder
public class IssueConstantDto {

    private String id;
    private String name;
}
