package me.potic.aggregator.controller

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import me.potic.aggregator.service.LatestSectionService
import me.potic.aggregator.service.LongSectionService
import me.potic.aggregator.service.ShortSectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import java.security.Principal

@RestController
@Slf4j
class ChunksController {

    @Autowired
    LatestSectionService latestSectionService

    @Autowired
    ShortSectionService shortSectionService

    @Autowired
    LongSectionService longSectionService

    @Timed(name = 'user.me.section.latest')
    @CrossOrigin
    @GetMapping(path = '/user/me/section/latest')
    @ResponseBody SectionChunk latestSectionChunkById(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        log.info "receive request for /user/me/section/latest"

        try {
            return latestSectionService.fetchChunk(principal.token, cursorId, count)
        } catch (e) {
            log.error "request for /user/me/section/latest failed: $e.message", e
            throw new RuntimeException("receive request for /user/me/section/latest failed: $e.message", e)
        }
    }

    @Timed(name = 'user.me.section.short')
    @CrossOrigin
    @GetMapping(path = '/user/me/section/short')
    @ResponseBody SectionChunk shortArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        log.info "receive request for /user/me/section/short"

        try {
            return shortSectionService.fetchChunk(principal.token, cursorId, count)
        } catch (e) {
            log.error "request for /user/me/section/short failed: $e.message", e
            throw new RuntimeException("request for /user/me/section/short failed: $e.message", e)
        }
    }

    @Timed(name = 'user.me.section.long')
    @CrossOrigin
    @GetMapping(path = '/user/me/section/long')
    @ResponseBody SectionChunk longArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        log.info "receive request for /user/me/section/long"

        try {
            return longSectionService.fetchChunk(principal.token, cursorId, count)
        } catch (e) {
            log.error "request for /user/me/section/long failed: $e.message", e
            throw new RuntimeException("request for /user/me/section/long failed: $e.message", e)
        }
    }
}
