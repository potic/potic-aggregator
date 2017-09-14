package me.potic.aggregator.service

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LongSectionService {

    static final Integer SECTION_SIZE = 5

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    ArticlesService articlesService

    Section fetchSectionHead(String accessToken) {
        Section.builder()
                .id('long')
                .title('latest long reads')
                .type('expandable')
                .firstChunk(fetchChunk(accessToken, null, SECTION_SIZE))
                .build()
    }

    @Timed(name = 'fetchChunk')
    SectionChunk fetchChunk(String accessToken, String cursorId, int count) {
        List longArticles = articlesService.retrieveUnreadLongArticlesOfUser(accessToken, LONGREAD_THRESHOLD, cursorId, count)
        return SectionChunk.builder().articles(longArticles).nextCursorId(longArticles.last().id).build()
    }
}
