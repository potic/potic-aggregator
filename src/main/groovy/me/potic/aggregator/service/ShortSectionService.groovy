package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ShortSectionService {

    static final Integer SECTION_SIZE = 5

    static final Integer REQUEST_SIZE = 25

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    ArticlesService articlesService

    SectionChunk fetchChunkById(String id) {
        int pageIndex = Integer.parseInt(id.split(':')[0])
        int pageOffset = Integer.parseInt(id.split(':')[1])

        List shortArticles = []

        int lastAddedCount

        while (shortArticles.size() < SECTION_SIZE) {
            List response = articlesService.unreadForSandboxUser(pageIndex, REQUEST_SIZE)

            if (response != null && response.size() > 0) {
                List shorts = response.drop(pageOffset).findAll({ it.wordCount < LONGREAD_THRESHOLD })
                lastAddedCount = shorts.size()
                shortArticles.addAll(shorts)
            } else {
                lastAddedCount = 0
            }

            pageIndex++
            pageOffset = 0
        }

        if (shortArticles.size() == SECTION_SIZE) {
            return SectionChunk.builder().id(id).articles(shortArticles).nextChunkId("${pageIndex}:0").build()
        } else {
            String nextId = "${pageIndex - 1}:${lastAddedCount - shortArticles.size() + SECTION_SIZE}"
            return SectionChunk.builder().id(id).articles(shortArticles.take(SECTION_SIZE)).nextChunkId(nextId).build()
        }
    }
}
