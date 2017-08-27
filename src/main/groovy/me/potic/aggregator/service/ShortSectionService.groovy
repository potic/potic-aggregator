package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ShortSectionService {

    static final Integer SECTION_SIZE = 5

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    ArticlesService articlesService

    Section fetchSectionHead(String userId) {
        Section.builder()
                .id('short')
                .title('latest short articles')
                .type('expandable')
                .firstChunk(fetchChunk(userId, null, SECTION_SIZE))
                .build()
    }

    SectionChunk fetchChunk(String userId, String cursorId, int count) {
        List shortArticles = articlesService.retrieveUnreadShortArticlesOfUser(userId, LONGREAD_THRESHOLD, cursorId, count)

        SectionChunk.builder().articles(shortArticles).nextCursorId(shortArticles.last().id).build()
    }
}
