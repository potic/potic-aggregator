FROM openjdk:8

RUN mkdir -p /usr/src/potic-sections && mkdir -p /usr/app

COPY build/distributions/* /usr/src/potic-sections/

RUN unzip /usr/src/potic-sections/potic-sections-*.zip -d /usr/app/ && ln -s /usr/app/potic-sections-* /usr/app/potic-sections

WORKDIR /usr/app/potic-sections

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-sections --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
