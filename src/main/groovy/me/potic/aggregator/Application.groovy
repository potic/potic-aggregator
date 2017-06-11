package me.potic.aggregator

import groovyx.net.http.HttpBuilder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    static final String ARTICLES_SERVICE_URL = 'http://pocket-square-articles:8080/'

    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }

    @Bean
    HttpBuilder articlesRest() {
        HttpBuilder.configure {
            request.uri = ARTICLES_SERVICE_URL
        }
    }
}