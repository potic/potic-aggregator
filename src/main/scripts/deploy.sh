#!/usr/bin/env sh

##############################################################################
##
##  Stop and kill currently running docker image, pull newest version and
##  run it.
##
##############################################################################

warn ( ) {
    echo "$*"
}

warn "Currently running docker images"
docker ps -a

warn "Pulling latest docker image..."
docker pull potic/potic-sections:$TAG_TO_DEPLOY

warn "Killing currently running docker image..."
docker kill potic-sections; docker rm potic-sections

warn "Starting docker image..."
docker run -dit --name potic-sections --restart on-failure --link potic-articles --link potic-users --link potic-models --link potic-feedback -e LOG_PATH=/mnt/logs -e LOGZIO_TOKEN=$LOGZIO_TOKEN -v /mnt/logs:/mnt/logs -p 40401:8080 potic/potic-sections:$TAG_TO_DEPLOY

warn "Wait 30sec to check status"
sleep 30

warn "Currently running docker images"
docker ps -a
