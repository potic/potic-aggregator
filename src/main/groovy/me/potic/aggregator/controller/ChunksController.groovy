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
    @GetMapping(path = '/user/me/section/latest?cursorId={cursorId}&count={count}')
    @ResponseBody SectionChunk latestSectionChunkById(@PathVariable(required = false) String cursorId, @PathVariable Integer count, final Principal principal) {
        timedService.timed "/user/me/section/latest?cursorId=${cursorId}&count=${count} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            latestSectionService.fetchChunk(pocketSquareId, cursorId, count)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/short?cursorId={cursorId}&count={count}')
    @ResponseBody SectionChunk shortArticlesSection(@PathVariable(required = false) String cursorId, @PathVariable Integer count, final Principal principal) {
        timedService.timed "/user/me/section/section/short?cursorId=${cursorId}&count=${count} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            shortSectionService.fetchChunk(pocketSquareId, cursorId, count)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/long?cursorId={cursorId}&count={count}')
    @ResponseBody SectionChunk longArticlesSection(@PathVariable(required = false) String cursorId, @PathVariable Integer count, final Principal principal) {
        timedService.timed "/user/me/section/section/long?cursorId=${cursorId}&count=${count} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            longSectionService.fetchChunk(pocketSquareId, cursorId, count)
        }
    }
}
