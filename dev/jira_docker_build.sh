#!/usr/bin/env bash

source env.sh

declare -r DEV_HOME=${PROJECT_FOLDER}/dev

docker build -f ${DEV_HOME}/jira/Dockerfile -t jira ${PROJECT_FOLDER}
