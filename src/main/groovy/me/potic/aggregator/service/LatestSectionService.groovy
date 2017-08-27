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
                .firstChunk(fetchChunk(userId, null, SECTION_SIZE))
                .build()
    }

    SectionChunk fetchChunk(String userId, String cursorId, int count) {
        List latestArticles = articlesService.retrieveUnreadArticlesOfUser(userId, cursorId, count)

        SectionChunk.builder().articles(latestArticles).nextCursorId(latestArticles.last().id).build()
    }
}
