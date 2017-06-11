package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import org.apache.commons.lang3.RandomUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import static groovyx.gpars.GParsPool.withPool

@Service
@Slf4j
class RandomSectionService {

    static final Integer SECTION_SIZE = 5

    @Autowired
    ArticlesService articlesService

    SectionChunk fetchChunkById() {
        Set randomIndexes = []

        while (randomIndexes.size() < SECTION_SIZE) {
            randomIndexes << RandomUtils.nextInt(SECTION_SIZE + 1, 100)
        }

        withPool {
            List randomArticles = randomIndexes.collectParallel { Integer randomIndex ->
                articlesService.unreadForSandboxUser(randomIndex, 1).first()
            }

            SectionChunk.builder().id('0').articles(randomArticles).nextChunkId('0').build()
        }
    }
}
