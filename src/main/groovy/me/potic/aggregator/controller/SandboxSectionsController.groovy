package me.potic.aggregator.controller

import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.Promise
import me.potic.aggregator.domain.Section
import me.potic.aggregator.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static groovyx.gpars.GParsPool.withPool

@RestController
@Slf4j
class SandboxSectionsController {

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
    @GetMapping(path = '/sandbox/section')
    @ResponseBody List<Section> sandboxSections() {
        timedService.timed '/sandbox/section request', {
            withPool {
                Promise latestArticlesSection = this.&latestArticlesSection.asyncFun().call()
                Promise randomArticlesSection = this.&randomArticlesSection.asyncFun().call()
                Promise shortArticlesSection = this.&shortArticlesSection.asyncFun().call()
                Promise longArticlesSection = this.&longArticlesSection.asyncFun().call()

                return [
                        latestArticlesSection.get(),
                        randomArticlesSection.get(),
                        shortArticlesSection.get(),
                        longArticlesSection.get()
                ]
            }
        }
    }

    @GetMapping(path = '/sandbox/section/latest')
    @ResponseBody Section latestArticlesSection() {
        timedService.timed "/sandbox/section/latest request", {
            Section.builder()
                    .id('latest')
                    .title('latest articles')
                    .type('expandable')
                    .firstChunk(latestSectionService.fetchChunkById('0'))
                    .build()
        }
    }

    @GetMapping(path = '/sandbox/section/random')
    @ResponseBody Section randomArticlesSection() {
        timedService.timed "/sandbox/section/random request", {
            Section.builder()
                    .id('random')
                    .title('random articles')
                    .type('fixed')
                    .firstChunk(randomSectionService.fetchChunkById())
                    .build()
        }
    }

    @GetMapping(path = '/sandbox/section/short')
    @ResponseBody Section shortArticlesSection() {
        timedService.timed "  /sandbox/section/short request", {
            Section.builder()
                    .id('short')
                    .title('latest short articles')
                    .type('expandable')
                    .firstChunk(shortSectionService.fetchChunkById('0:0'))
                    .build()
        }
    }

    @GetMapping(path = '/sandbox/section/long')
    @ResponseBody Section longArticlesSection() {
        timedService.timed "  /sandbox/section/long request", {
            Section.builder()
                    .id('long')
                    .title('latest long reads')
                    .type('expandable')
                    .firstChunk(longSectionService.fetchChunkById('0:0'))
                    .build()
        }
    }
}
