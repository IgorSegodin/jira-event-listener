JIRA event listener, which redirects all issue events to RabbitMQ topic.

### Installation

Build project using command `gradlew build`

Copy `build/libs/jira-event-listener-0.0.1.jar` to JIRA install folder `/opt/atlassian/jira/atlassian-jira/WEB-INF/lib/`

After JIRA startup, go to: Settings -> System -> Listeners

Add class: `org.isegodin.jira.event.listener.RedirectToRabbitJiraEventListener`

Press Edit and specify parameters:
* rabbit.host
* rabbit.port - optional, `5672` by default
* rabbit.username
* rabbit.password
* rabbit.topic - topic name
* gzip.enable - optional, `false` by default

Tested on Atlassian JIRA Version: 7.13.1

### Development

Create `dev/env.sh` from example `dev/env.sh.example`

Create docker network with command `dev/network_docker_create.sh`

Build JIRA image `dev/jira_docker_build.sh`. Changes to jar require image rebuild

Run RabbitMQ and JIRA in different terminal windows
* `dev/rabbit_docker_run.sh`
* `dev/jira_docker_run.sh`

Java debug available on port `5005`

See RabbitMQ tutorial for topic listener example: https://www.rabbitmq.com/tutorials/tutorial-five-java.html
