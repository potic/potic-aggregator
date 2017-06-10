package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Section
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static java.util.Collections.shuffle

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
        [
                latestArticlesSection(),
                randomArticlesSection(),
                shortArticlesSection(),
                longArticlesSection()
        ]
    }

    private Section latestArticlesSection() {
        List latestArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: SANDBOX_SECTION_SIZE ]
        }

        Section.builder().name('latest articles').articles(latestArticles).build()
    }

    private Section randomArticlesSection() {
        List allArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: 100 ]
        }

        allArticles = allArticles.drop(SANDBOX_SECTION_SIZE)
        shuffle(allArticles)
        List randomArticles = allArticles.take(SANDBOX_SECTION_SIZE)

        Section.builder().name('random articles').articles(randomArticles).build()
    }

    private Section shortArticlesSection() {
        List allArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: 100 ]
        }

        List shortArticles = allArticles.findAll({ it.wordCount < LONGREAD_THRESHOLD }).take(SANDBOX_SECTION_SIZE)

        Section.builder().name('short articles').articles(shortArticles).build()
    }

    private Section longArticlesSection() {
        List allArticles = articlesService.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: 0, size: 100 ]
        }

        List longArticles = allArticles.findAll({ it.wordCount >= LONGREAD_THRESHOLD }).take(SANDBOX_SECTION_SIZE)

        Section.builder().name('longreads').articles(longArticles).build()
    }
}
