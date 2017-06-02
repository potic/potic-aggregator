package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Section
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Slf4j
class SandboxAggregationController {

    static final String SANDBOX_USER_ID = '58b1800dc9e77c0001d1d702'
    static final Integer SANDBOX_SECTION_SIZE = 5

    @Autowired
    HttpBuilder articlesService

    @CrossOrigin
    @GetMapping(path = '/sandbox/section')
    @ResponseBody List<Section> sandboxSections() {
        [ latestArticlesSection(), randomArticlesSection() ]
    }

    private Section latestArticlesSection() {
        List latestArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: SANDBOX_SECTION_SIZE ]
        }

        Section.builder().name('latest articles').articles(latestArticles).build()
    }

    private Section randomArticlesSection() {
        Set randomArticles = [] as Set

        while (randomArticles.size() < SANDBOX_SECTION_SIZE) {
            List randomResponse = articlesService.get {
                request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
                request.uri.query = [page: RandomUtils.nextInt(0, 100), size: 1 ]
            }

            if (randomResponse != null && randomResponse.size() > 0) {
                randomArticles << randomResponse.first()
            }
        }

        Section.builder().name('random articles').articles(randomArticles as List).build()
    }
}
