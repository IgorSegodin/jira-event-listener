#!/usr/bin/env bash

source env.sh

docker run -it --rm -p 8090:15672 -p 5672:5672 \
--network=local-net \
--hostname=rabbitmq \
--name=rabbitmq \
-e RABBITMQ_DEFAULT_USER=${RABBIT_USERNAME} \
-e RABBITMQ_DEFAULT_PASS=${RABBIT_PASSWORD} \
\
rabbitmq:3.7-alpine