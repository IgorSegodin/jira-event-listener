#!/usr/bin/env bash

source env.sh

declare -r JIRA_HOME=${PROJECT_FOLDER}/dev/jira_home

if [ ! -d "$JIRA_HOME" ]; then
  mkdir ${JIRA_HOME}
fi

docker run -it --rm -p 8080:8080 -p 5005:5005 \
--network=local-net \
--hostname=jira \
--name=jira \
-v ${JIRA_HOME}:/var/atlassian/jira \
-e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" \
\
jira
