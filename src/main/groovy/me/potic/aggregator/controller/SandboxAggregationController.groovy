package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.Promise
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Section
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static groovyx.gpars.GParsPool.withPool

@RestController
@Slf4j
class SandboxAggregationController {

    static final String SANDBOX_USER_ID = '58b1800dc9e77c0001d1d702'
    static final Integer SANDBOX_SECTION_SIZE = 5

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    HttpBuilder articlesService

    @CrossOrigin
    @GetMapping(path = '/sandbox/section')
    @ResponseBody List<Section> sandboxSections() {
        withPool {
            Promise latestArticlesSection = this.&latestArticlesSection.asyncFun().call()
            Promise randomArticlesSection = this.&randomArticlesSection.asyncFun().call()
            Promise shortArticlesSection = this.&shortArticlesSection.asyncFun().call()
            Promise longArticlesSection = this.&longArticlesSection.asyncFun().call()

            return [
                    latestArticlesSection.get(),
                    randomArticlesSection.get(),
                    shortArticlesSection.get(),
                    longArticlesSection.get()
            ]
        }
    }

    private Section latestArticlesSection() {
        List latestArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: SANDBOX_SECTION_SIZE ]
        }

        Section.builder().name('latest articles').articles(latestArticles).build()
    }

    private Section randomArticlesSection() {
        Set randomArticles = []

        while (randomArticles.size() < SANDBOX_SECTION_SIZE) {
            List randomResponse = articlesService.get {
                request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
                request.uri.query = [ page: RandomUtils.nextInt(SANDBOX_SECTION_SIZE + 1, 100), size: 1 ]
            }

            if (randomResponse != null && randomResponse.size() > 0) {
                randomArticles << randomResponse.first()
            }
        }

        Section.builder().name('random articles').articles(randomArticles as List).build()
    }

    private Section shortArticlesSection() {
        List shortArticles = []
        int requestSize = SANDBOX_SECTION_SIZE

        while (shortArticles.size() < SANDBOX_SECTION_SIZE) {
            List response = articlesService.get {
                request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
                request.uri.query = [ page: 0, size: requestSize ]
            }

            if (response != null && response.size() > 0) {
                shortArticles = response.findAll { it.wordCount < LONGREAD_THRESHOLD }
            }
            requestSize++
        }

        Section.builder().name('short articles').articles(shortArticles).build()
    }

    private Section longArticlesSection() {
        List longArticles = []
        int requestSize = SANDBOX_SECTION_SIZE

        while (longArticles.size() < SANDBOX_SECTION_SIZE) {
            List response = articlesService.get {
                request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
                request.uri.query = [ page: 0, size: requestSize ]
            }

            if (response != null && response.size() > 0) {
                longArticles = response.findAll { it.wordCount >= LONGREAD_THRESHOLD }
            }
            requestSize++
        }

        Section.builder().name('longreads').articles(longArticles).build()
    }
}
