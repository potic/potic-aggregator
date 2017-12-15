package me.potic.sections.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class RankerService {

    HttpBuilder rankerServiceRest

    @Autowired
    HttpBuilder rankerServiceRest(@Value('${services.ranker.url}') String rankerServiceUrl) {
        rankerServiceRest = HttpBuilder.configure {
            request.uri = rankerServiceUrl
        }
    }

    String getActualRankId() {
        log.debug "requesting actual rank id..."

        try {
            String response = rankerServiceRest.get(String) {
                request.uri.path = '/actual'
                request.contentType = 'application/json'
            }

            return response
        } catch (e) {
            log.error "requesting actual rank id failed: $e.message", e
            throw new RuntimeException("requesting actual rank id failed", e)
        }
    }
}
