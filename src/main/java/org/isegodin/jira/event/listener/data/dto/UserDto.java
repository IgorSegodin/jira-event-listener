package org.isegodin.jira.event.listener.data.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author isegodin
 */
@Data
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String emailAddress;
    private String displayName;
}
