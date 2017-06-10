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

    static final Integer REQUEST_SIZE = 25

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    HttpBuilder articlesService

    @CrossOrigin
    @GetMapping(path = '/sandbox/section')
    @ResponseBody List<Section> sandboxSections() {
        timed '/sandbox/section request', {
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
    }

    private Section latestArticlesSection() {
        timed "  preparing 'latest articles' section", {
            List latestArticles = requestArticles(0, SANDBOX_SECTION_SIZE)
            Section.builder().name('latest articles').articles(latestArticles).build()
        }
    }

    private Section randomArticlesSection() {
        timed "  preparing 'random articles' section", {
            Set randomIndexes = []

            while (randomIndexes.size() < SANDBOX_SECTION_SIZE) {
                randomIndexes << RandomUtils.nextInt(SANDBOX_SECTION_SIZE + 1, 100)
            }

            withPool {
                List randomArticles = randomIndexes.collectParallel {
                    requestArticles(it, 1).first()
                }

                Section.builder().name('random articles').articles(randomArticles).build()
            }
        }
    }

    private Section shortArticlesSection() {
        timed "  preparing 'latest short articles' section", {
            List shortArticles = []
            int pageIndex = 0

            while (shortArticles.size() < SANDBOX_SECTION_SIZE) {
                List response = requestArticles(pageIndex, REQUEST_SIZE)

                if (response != null && response.size() > 0) {
                    shortArticles.addAll(response.findAll({ it.wordCount < LONGREAD_THRESHOLD }))
                }

                pageIndex++
            }

            Section.builder().name('latest short articles').articles(shortArticles.take(SANDBOX_SECTION_SIZE)).build()
        }
    }

    private Section longArticlesSection() {
        timed "  preparing 'latest long reads' section", {
            List longArticles = []
            int pageIndex = 0

            while (longArticles.size() < SANDBOX_SECTION_SIZE) {
                List response = requestArticles(pageIndex, REQUEST_SIZE)

                if (response != null && response.size() > 0) {
                    longArticles.addAll(response.findAll({ it.wordCount >= LONGREAD_THRESHOLD }))
                }

                pageIndex++
            }

            Section.builder().name('latest long reads').articles(longArticles.take(SANDBOX_SECTION_SIZE)).build()
        }
    }

    private List<Article> requestArticles(int page, int size) {
        timed "    requesting $size articles from page #$page", {
            articlesService.get {
                request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
                request.uri.query = [ page: page, size: size ]
            }
        }
    }

    private <T> T timed(String name, Closure<T> action) {
        log.info "$name started"
        long startTime = System.currentTimeMillis()

        try {
            return action.call()
        } finally {
            log.info "$name finished in ${(System.currentTimeMillis() - startTime)/1000}s"
        }
    }
}
