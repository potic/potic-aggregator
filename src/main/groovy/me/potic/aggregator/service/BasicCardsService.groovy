package me.potic.aggregator.service

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Card
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
@Slf4j
class BasicCardsService {

    HttpBuilder cardsServiceRest

    @Bean
    HttpBuilder cardsServiceRest(@Value('${services.basicCards.url}') String cardsServiceUrl) {
        cardsServiceRest = HttpBuilder.configure {
            request.uri = cardsServiceUrl
        }
    }

    @Timed(name = 'getUserCards')
    List<Card> getUserCards(String accessToken, String cursorId, Integer count, Integer minLength, Integer maxLength) {
        log.info "requesting $count cards with articles longer than $minLength and shorter than $maxLength from cursor $cursorId"

        try {
            def query = [:]
            if (cursorId != null) {
                query['cursorId'] = cursorId
            }
            if (count != null) {
                query['count'] = count
            }
            if (minLength != null) {
                query['minLength'] = minLength
            }
            if (maxLength != null) {
                query['maxLength'] = maxLength
            }

            List response = cardsServiceRest.get(List) {
                request.headers['Authorization'] = "Bearer $accessToken".toString()

                request.uri.path = '/user/me/cards/basic'
                request.uri.query = query
            }

            return response.collect({ new Card(it) })
        } catch (e) {
            log.error "requesting $count cards with articles longer than $minLength and shorter than $maxLength from cursor $cursorId failed: $e.message", e
            throw new RuntimeException("requesting $count cards with articles longer than $minLength and shorter than $maxLength from cursor $cursorId failed: $e.message", e)
        }
    }
}
