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

warn "Killing currently running docker image..."
docker kill potic-sections; docker rm potic-sections

warn "Pulling latest docker image..."
docker pull potic/potic-sections:$TAG_TO_DEPLOY

warn "Starting docker image..."
docker run -dit --name potic-sections --link potic-articles --link potic-users --link potic-ranker -e LOG_PATH=/mnt/logs -v /mnt/logs:/mnt/logs -p 40401:8080 potic/potic-sections:$TAG_TO_DEPLOY

warn "Currently running docker images"
docker ps -a
