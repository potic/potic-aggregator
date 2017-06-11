package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.domain.SectionChunk
import me.potic.aggregator.service.LatestSectionService
import me.potic.aggregator.service.LongSectionService
import me.potic.aggregator.service.RandomSectionService
import me.potic.aggregator.service.ShortSectionService
import me.potic.aggregator.service.TimedService
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

    @CrossOrigin
    @GetMapping(path = '/sandbox/section/latest/{chunkId}')
    @ResponseBody SectionChunk latestSectionChunkById(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/latest/${chunkId} request", {
            latestSectionService.fetchChunkById(chunkId)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/sandbox/section/random/{chunkId}')
    @ResponseBody Section randomArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/random/${chunkId} request", {
            randomSectionService.fetchChunkById()
        }
    }

    @CrossOrigin
    @GetMapping(path = '/sandbox/section/short/{chunkId}')
    @ResponseBody Section shortArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/short/${chunkId} request", {
            shortSectionService.fetchChunkById(chunkId)
        }
    }

    @CrossOrigin
    @GetMapping(path = '/sandbox/section/long/{chunkId}')
    @ResponseBody Section longArticlesSection(@PathVariable String chunkId) {
        timedService.timed "/sandbox/section/long/${chunkId} request", {
            longSectionService.fetchChunkById(chunkId)
        }
    }
}
