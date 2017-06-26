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
docker kill potic-aggregator; docker rm potic-aggregator

warn "Pulling latest docker image..."
docker pull potic/potic-aggregator:$TAG_TO_DEPLOY

warn "Starting docker image..."
docker run -dit --name potic-aggregator --link pocket-square-articles -v /logs:/logs -p 40401:8080 potic/potic-aggregator:$TAG_TO_DEPLOY

warn "Currently running docker images"
docker ps -a
