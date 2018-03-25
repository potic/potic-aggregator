package me.potic.sections.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.sections.domain.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class ModelsService {

    HttpBuilder modelsServiceRest

    @Autowired
    HttpBuilder modelsServiceRest(@Value('${services.models.url}') String modelsServiceUrl) {
        modelsServiceRest = HttpBuilder.configure {
            request.uri = modelsServiceUrl
        }
    }

    Model getActualModel() {
        log.debug "requesting actual model..."

        try {
            def response = modelsServiceRest.get {
                request.uri.path = '/actual'
                request.contentType = 'application/json'
            }

            return new Model(response)
        } catch (e) {
            log.error "requesting actual rank id failed: $e.message", e
            throw new RuntimeException("requesting actual rank id failed", e)
        }
    }
}
