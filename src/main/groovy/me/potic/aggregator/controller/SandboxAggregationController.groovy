package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Section
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
class SandboxAggregationController {

    static final String ARTICLES_SERVICE_URL = 'http://pocket-square-articles:8080/'

    static final String SANDBOX_USER_ID = '58b1800dc9e77c0001d1d702'
    static final Integer SANDBOX_PAGE_INDEX = 0
    static final Integer SANDBOX_PAGE_SIZE = 5

    @CrossOrigin
    @GetMapping(path = '/sandbox/section')
    @ResponseBody List<Section> sandboxSections() {
        HttpBuilder articlesService = HttpBuilder.configure {
            request.uri = ARTICLES_SERVICE_URL
        }

        def response = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: SANDBOX_PAGE_INDEX, size: SANDBOX_PAGE_SIZE ]
        }

        return [ Section.builder().name('latest articles').articles(response).build() ]
    }
}
