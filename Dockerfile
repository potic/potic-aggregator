FROM openjdk:8

RUN mkdir -p /usr/src/potic-sections && mkdir -p /opt

COPY build/distributions/* /usr/src/potic-sections/

RUN unzip /usr/src/potic-sections/potic-sections-*.zip -d /opt/ && ln -s /opt/potic-sections-* /opt/potic-sections

WORKDIR /opt/potic-sections

EXPOSE 8080
ENV ENVIRONMENT_NAME test
ENTRYPOINT [ "sh", "-c", "./bin/potic-sections --spring.profiles.active=$ENVIRONMENT_NAME" ]
CMD []
