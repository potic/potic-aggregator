package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import me.potic.aggregator.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

import java.security.Principal

@RestController
@Slf4j
class ChunksController {

    @Autowired
    TimedService timedService

    @Autowired
    LatestSectionService latestSectionService

    @Autowired
    ShortSectionService shortSectionService

    @Autowired
    LongSectionService longSectionService

    @Autowired
    UserService userService

    @CrossOrigin
    @GetMapping(path = '/user/me/section/latest')
    @ResponseBody SectionChunk latestSectionChunkById(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        timedService.timed "/user/me/section/latest?cursorId=${cursorId}&count=${count} request", {
            latestSectionService.fetchChunk(principal.token, cursorId, count)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/short')
    @ResponseBody SectionChunk shortArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        timedService.timed "/user/me/section/section/short?cursorId=${cursorId}&count=${count} request", {
            shortSectionService.fetchChunk(principal.token, cursorId, count)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/long')
    @ResponseBody SectionChunk longArticlesSection(@RequestParam(value = 'cursorId', required = false) String cursorId, @RequestParam(value = 'count') Integer count, final Principal principal) {
        timedService.timed "/user/me/section/section/long?cursorId=${cursorId}&count=${count} request", {
            longSectionService.fetchChunk(principal.token, cursorId, count)
        }
    }
}
