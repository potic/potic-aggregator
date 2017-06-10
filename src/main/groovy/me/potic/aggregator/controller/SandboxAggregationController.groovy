package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.Promise
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Article
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
        log.info '/sandbox/section request received'

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
        log.info "preparing 'latest articles' section..."

        List latestArticles = requestArticles(0, SANDBOX_SECTION_SIZE)

        Section.builder().name('latest articles').articles(latestArticles).build()
    }

    private Section randomArticlesSection() {
        log.info "preparing 'random articles' section..."

        Set randomArticles = []

        while (randomArticles.size() < SANDBOX_SECTION_SIZE) {
            List randomResponse = requestArticles(RandomUtils.nextInt(SANDBOX_SECTION_SIZE + 1, 100), 1)

            if (randomResponse != null && randomResponse.size() > 0) {
                randomArticles << randomResponse.first()
            }
        }

        Section.builder().name('random articles').articles(randomArticles as List).build()
    }

    private Section shortArticlesSection() {
        log.info "preparing 'latest short articles' section..."

        List shortArticles = []
        int requestSize = SANDBOX_SECTION_SIZE

        while (shortArticles.size() < SANDBOX_SECTION_SIZE) {
            List response = requestArticles(0, requestSize)

            if (response != null && response.size() > 0) {
                shortArticles = response.findAll { it.wordCount < LONGREAD_THRESHOLD }
            }
            requestSize++
        }

        Section.builder().name('latest short articles').articles(shortArticles).build()
    }

    private Section longArticlesSection() {
        log.info "preparing 'latest long reads' section..."

        List longArticles = []
        int requestSize = SANDBOX_SECTION_SIZE

        while (longArticles.size() < SANDBOX_SECTION_SIZE) {
            List response = requestArticles(0, requestSize)

            if (response != null && response.size() > 0) {
                longArticles = response.findAll { it.wordCount >= LONGREAD_THRESHOLD }
            }
            requestSize++
        }

        Section.builder().name('latest long reads').articles(longArticles).build()
    }

    private List<Article> requestArticles(int page, int size) {
        log.info "requesting $size articles from page #$page"

        articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: page, size: size ]
        }
    }
}
