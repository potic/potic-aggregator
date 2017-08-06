package me.potic.aggregator

import groovyx.net.http.HttpBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }

    @Bean
    HttpBuilder articlesRest(@Value('${services.articles.url}') String articlesServiceUrl) {
        HttpBuilder.configure {
            request.uri = articlesServiceUrl
        }
    }

    @Bean
    HttpBuilder auth0Rest(@Value('${services.auth0.url}') String auth0ServiceUrl) {
        HttpBuilder.configure {
            request.uri = auth0ServiceUrl
        }
    }
}