package me.potic.aggregator.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.service.LatestSectionService
import me.potic.aggregator.service.LongSectionService
import me.potic.aggregator.service.ShortSectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

import static groovyx.gpars.GParsPool.executeAsync
import static groovyx.gpars.GParsPool.withPool

@RestController
@Slf4j
class SectionsController {

    @Autowired
    LatestSectionService latestSectionService

    @Autowired
    ShortSectionService shortSectionService

    @Autowired
    LongSectionService longSectionService

    @Timed(name = 'user.me.section')
    @CrossOrigin
    @GetMapping(path = '/user/me/section')
    @ResponseBody List<Section> userSections(final Principal principal) {
        log.info "receive request for /user/me/section"

        try {
            return withPool {
                executeAsync(
                        { latestSectionService.fetchSectionHead(principal.token) },
                        { shortSectionService.fetchSectionHead(principal.token) },
                        { longSectionService.fetchSectionHead(principal.token) }
                ).collect { promiseOnSection -> promiseOnSection.get() }
            }
        } catch (e) {
            log.error "request for /user/me/section failed: $e.message", e
            throw new RuntimeException("request for /user/me/section failed: $e.message", e)
        }
    }
}
