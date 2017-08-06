package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LatestSectionService {

    static final Integer SECTION_SIZE = 5

    @Autowired
    ArticlesService articlesService

    Section fetchSectionHead(String userId) {
        Section.builder()
                .id('latest')
                .title('latest articles')
                .type('expandable')
                .firstChunk(fetchChunkById(userId, '0'))
                .build()
    }

    SectionChunk fetchChunkById(String userId, String chunkId) {
        Integer page = Integer.parseInt(chunkId)

        List latestArticles = articlesService.retrieveUnreadArticlesOfUser(userId, page, SECTION_SIZE)

        SectionChunk.builder().id(chunkId).articles(latestArticles).nextChunkId("${page + 1}").build()
    }
}
