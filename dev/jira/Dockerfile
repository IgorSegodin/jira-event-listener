FROM cptactionhank/atlassian-jira:latest

COPY "build/libs/jira-event-listener-0.0.1.jar" "/opt/atlassian/jira/atlassian-jira/WEB-INF/lib/jira-event-listener-0.0.1.jar"

# Run Atlassian JIRA as a foreground process by default.
CMD ["/opt/atlassian/jira/bin/start-jira.sh", "-fg"]