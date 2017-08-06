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
    RandomSectionService randomSectionService

    @Autowired
    ShortSectionService shortSectionService

    @Autowired
    LongSectionService longSectionService

    @Autowired
    UserService userService

    @CrossOrigin
    @GetMapping(path = '/user/me/section/latest/{chunkId}')
    @ResponseBody SectionChunk latestSectionChunkById(@PathVariable String chunkId, final Principal principal) {
        timedService.timed "/user/me/section/section/latest/${chunkId} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            latestSectionService.fetchChunkById(pocketSquareId, chunkId)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/random/{chunkId}')
    @ResponseBody SectionChunk randomArticlesSection(@PathVariable String chunkId, final Principal principal) {
        timedService.timed "/user/me/section/section/random/${chunkId} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            randomSectionService.fetchChunkById(pocketSquareId)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/short/{chunkId}')
    @ResponseBody SectionChunk shortArticlesSection(@PathVariable String chunkId, final Principal principal) {
        timedService.timed "/user/me/section/section/short/${chunkId} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            shortSectionService.fetchChunkById(pocketSquareId, chunkId)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/user/me/section/long/{chunkId}')
    @ResponseBody SectionChunk longArticlesSection(@PathVariable String chunkId, final Principal principal) {
        timedService.timed "/user/me/section/section/long/${chunkId} request", {
            String pocketSquareId = userService.fetchPocketSquareIdByAuth0Token(principal.token)

            longSectionService.fetchChunkById(pocketSquareId, chunkId)
        }
    }
}
