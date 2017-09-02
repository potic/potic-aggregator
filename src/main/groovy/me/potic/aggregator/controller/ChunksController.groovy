package me.potic.aggregator.controller

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import me.potic.aggregator.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import javax.annotation.PostConstruct
import java.security.Principal

import static com.codahale.metrics.MetricRegistry.name

@RestController
@Slf4j
class ChunksController {

    @Autowired
    LatestSectionService latestSectionService

    @Autowired
    ShortSectionService shortSectionService

    @Autowired
    LongSectionService longSectionService

    @Autowired
    MetricRegistry metricRegistry

    Timer latestSectionChunkByIdTimer

    Timer shortArticlesSectionTimer

    Timer longArticlesSectionTimer

    @PostConstruct
    void initMetrics() {
        latestSectionChunkByIdTimer = metricRegistry.timer(name('request', 'user', 'me', 'section', 'latest'))
        shortArticlesSectionTimer = metricRegistry.timer(name('request', 'user', 'me', 'section', 'short'))
        longArticlesSectionTimer = metricRegistry.timer(name('request', 'user', 'me', 'section', 'long'))
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/latest')
    @ResponseBody SectionChunk latestSectionChunkById(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        final Timer.Context timerContext = latestSectionChunkByIdTimer.time()
        log.info "receive request for /user/me/section/latest"
        try {
            return latestSectionService.fetchChunk(principal.token, cursorId, count)
        } finally {
            long time = timerContext.stop()
            log.info "request for /user/me/section/latest took ${time / 1_000_000}ms"
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/short')
    @ResponseBody SectionChunk shortArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        final Timer.Context timerContext = shortArticlesSectionTimer.time()
        log.info "receive request for /user/me/section/short"
        try {
            return shortSectionService.fetchChunk(principal.token, cursorId, count)
        } finally {
            long time = timerContext.stop()
            log.info "request for /user/me/section/short took ${time / 1_000_000}ms"
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/long')
    @ResponseBody SectionChunk longArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        final Timer.Context timerContext = longArticlesSectionTimer.time()
        log.info "receive request for /user/me/section/long"
        try {
            return longSectionService.fetchChunk(principal.token, cursorId, count)
        } finally {
            long time = timerContext.stop()
            log.info "request for /user/me/section/long took ${time / 1_000_000}ms"
        }
    }
}
