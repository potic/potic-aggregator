package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import me.potic.aggregator.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@Slf4j
class SandboxChunksController {

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

    @GetMapping(path = '/sandbox/section/latest/{chunkId}')
    @ResponseBody SectionChunk latestSectionChunkById(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/latest/${chunkId} request", {
            latestSectionService.fetchChunkById(chunkId)
        }
    }

    @GetMapping(path = '/sandbox/section/random/{chunkId}')
    @ResponseBody SectionChunk randomArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/random/${chunkId} request", {
            randomSectionService.fetchChunkById()
        }
    }

    @GetMapping(path = '/sandbox/section/short/{chunkId}')
    @ResponseBody SectionChunk shortArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/short/${chunkId} request", {
            shortSectionService.fetchChunkById(chunkId)
        }
    }

    @GetMapping(path = '/sandbox/section/long/{chunkId}')
    @ResponseBody SectionChunk longArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/long/${chunkId} request", {
            longSectionService.fetchChunkById(chunkId)
        }
    }
}
