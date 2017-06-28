FROM openjdk:8

RUN mkdir -p /usr/src/potic-aggregator && mkdir -p /usr/app

COPY build/distributions/* /usr/src/potic-aggregator/

RUN unzip /usr/src/potic-aggregator/potic-aggregator-*.zip -d /usr/app/ && ln -s /usr/app/potic-aggregator-* /usr/app/potic-aggregator

WORKDIR /usr/app/potic-aggregator

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-aggregator --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
