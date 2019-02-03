package org.isegodin.jira.event.listener.data.converter;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueConstant;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.user.ApplicationUser;
import org.isegodin.jira.event.listener.data.dto.CommentDto;
import org.isegodin.jira.event.listener.data.dto.EventDto;
import org.isegodin.jira.event.listener.data.dto.IssueConstantDto;
import org.isegodin.jira.event.listener.data.dto.IssueDto;
import org.isegodin.jira.event.listener.data.dto.UserDto;
import org.isegodin.jira.event.listener.data.dto.WorklogDto;

import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Convert {@link IssueEvent} to {@link EventDto}
 *
 * @author isegodin
 */
public class IssueEventToEventDtoConverter {

    public EventDto convert(IssueEvent source) {
        return EventDto.builder()
                .user(convertUser(source.getUser()))
                .issue(convertIssue(source.getIssue()))
                .worklog(convertWorklog(source.getWorklog()))
                .comment(convertComment(source.getComment()))
                .timestamp(source.getTime().toInstant().atOffset(ZoneOffset.UTC))
                .build();
    }

    private IssueDto convertIssue(Issue source) {
        return IssueDto.builder()
                .key(source.getKey())
                .number(source.getNumber())
                .projectId(source.getProjectId())
                .summary(source.getSummary())
                .assignee(convertUser(source.getAssignee()))
                .type(convertConstant(source.getIssueType()))
                .priority(convertConstant(source.getPriority()))
                .status(convertConstant(source.getStatus()))
                .build();
    }

    private UserDto convertUser(ApplicationUser user) {
        return Optional.ofNullable(user)
                .map(source -> UserDto.builder()
                        .id(source.getId())
                        .name(source.getName())
                        .emailAddress(source.getEmailAddress())
                        .displayName(source.getDisplayName())
                        .build())
                .orElse(null);
    }

    private IssueConstantDto convertConstant(IssueConstant constant) {
        return Optional.ofNullable(constant)
                .map(source -> IssueConstantDto.builder()
                        .id(source.getId())
                        .name(source.getName())
                        .build())
                .orElse(null);
    }

    private WorklogDto convertWorklog(Worklog worklog) {
        return Optional.ofNullable(worklog)
                .map(source -> WorklogDto.builder()
                        .id(source.getId())
                        .comment(source.getComment())
                        .startDate(source.getStartDate().toInstant().atOffset(ZoneOffset.UTC))
                        .timeSpent(source.getTimeSpent())
                        .build())
                .orElse(null);
    }

    private CommentDto convertComment(Comment comment) {
        return Optional.ofNullable(comment)
                .map(source -> CommentDto.builder()
                        .id(source.getId())
                        .body(source.getBody())
                        .created(source.getCreated().toInstant().atOffset(ZoneOffset.UTC))
                        .author(convertUser(source.getAuthorApplicationUser()))
                        .build())
                .orElse(null);
    }
}
