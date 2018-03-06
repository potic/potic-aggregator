package me.potic.sections.controller

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.sections.SectionFetcher
import me.potic.sections.domain.Article
import me.potic.sections.domain.Card
import me.potic.sections.domain.Section
import me.potic.sections.domain.User
import me.potic.sections.service.FeedbackService
import me.potic.sections.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.security.Principal

import static com.codahale.metrics.MetricRegistry.name
import static me.potic.sections.util.Utils.maskForLog

@RestController
@Slf4j
class SectionsController {

    @Autowired
    UserService userService

    @Autowired
    FeedbackService feedbackService

    @Autowired
    Collection<SectionFetcher> sectionFetchers

    @Autowired
    MetricRegistry metricRegistry

    @Timed(name = 'api.section')
    @CrossOrigin
    @GetMapping(path = '/section')
    @ResponseBody ResponseEntity<List<Section>> sectionsList(final Principal principal) {
        log.info "receive GET request for /section with token=${maskForLog(principal.token)}"

        try {
            List<Section> sections = sectionFetchers*.section().sort({ sectionFetcher -> sectionFetcher.priority })
            return new ResponseEntity<>(sections, HttpStatus.OK)
        } catch (e) {
            log.error "GET request for /section with token=${maskForLog(principal.token)} failed: $e.message", e
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @CrossOrigin
    @PostMapping(path = '/section/{sectionId}')
    @ResponseBody ResponseEntity<List<Card>> fetchCards(final Principal principal, @PathVariable('sectionId') String sectionId, @RequestBody Map fetchCardsRequest) {
        log.info "receive POST request for /section/${sectionId} with token=${maskForLog(principal.token)} and body=${fetchCardsRequest}"

        Timer.Context timer = metricRegistry.timer(name('api.section', sectionId)).time()
        try {
            User user = userService.findUserByAuth0Token(principal.token)
            SectionFetcher sectionFetcher = sectionFetchers.find { it.section().id == sectionId }
            List<Article> articles = sectionFetcher.fetch(user, fetchCardsRequest)
            articles.each { article -> feedbackService.showed(user, article) }
            if (fetchCardsRequest.skipIds != null) {
                fetchCardsRequest.skipIds.each { skipId -> feedbackService.skipped(user, skipId) }
            }
            return new ResponseEntity<>(articles*.card, HttpStatus.OK)
        } catch (e) {
            log.error "POST request for /section/${sectionId} with token=${maskForLog(principal.token)} and body=${fetchCardsRequest} failed: $e.message", e
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
        } finally {
            timer.stop()
        }
    }
}
