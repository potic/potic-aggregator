package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LongSectionService {

    static final Integer SECTION_SIZE = 5

    static final Integer REQUEST_SIZE = 25

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    ArticlesService articlesService

    SectionChunk fetchChunkById(String id) {
        int pageIndex = Integer.parseInt(id.split(':')[0])
        int pageOffset = Integer.parseInt(id.split(':')[1])

        List longArticles = []

        int lastAddedCount

        while (longArticles.size() < SECTION_SIZE) {
            List response = articlesService.unreadForSandboxUser(pageIndex, REQUEST_SIZE)

            if (response != null && response.size() > 0) {
                List longreads = response.drop(pageOffset).findAll({ it.wordCount >= LONGREAD_THRESHOLD })
                lastAddedCount = longreads.size()
                longArticles.addAll(longreads)
            } else {
                lastAddedCount = 0
            }

            pageIndex++
            pageOffset = 0
        }

        if (longArticles.size() == SECTION_SIZE) {
            return SectionChunk.builder().id(id).articles(longArticles).nextChunkId("${pageIndex}:0").build()
        } else {
            String nextId = "${pageIndex - 1}:${lastAddedCount - longArticles.size() + SECTION_SIZE}"
            return SectionChunk.builder().id(id).articles(longArticles.take(SECTION_SIZE)).nextChunkId(nextId).build()
        }
    }
}
