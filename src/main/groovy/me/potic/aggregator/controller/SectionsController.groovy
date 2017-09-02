package me.potic.aggregator.controller

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.annotation.PostConstruct
import java.security.Principal

import static com.codahale.metrics.MetricRegistry.name
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

    @Autowired
    MetricRegistry metricRegistry

    Timer userSectionsTimer

    @PostConstruct
    void initMetrics() {
        userSectionsTimer = metricRegistry.timer(name('request', 'user', 'me', 'section'))
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section')
    @ResponseBody List<Section> userSections(final Principal principal) {
        final Timer.Context timerContext = userSectionsTimer.time()
        log.info "receive request for /user/me/section"
        try {
            return withPool {
                executeAsync(
                        { latestSectionService.fetchSectionHead(principal.token) },
                        { shortSectionService.fetchSectionHead(principal.token) },
                        { longSectionService.fetchSectionHead(principal.token) }
                ).collect { promiseOnSection -> promiseOnSection.get() }
            }
        } finally {
            long time = timerContext.stop()
            log.info "request for /user/me/section took ${time / 1_000_000}ms"
        }
    }
}
