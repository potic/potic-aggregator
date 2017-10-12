package me.potic.aggregator.service

import com.codahale.metrics.annotation.Timed
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
    BasicCardsService basicCardsService

    Section fetchSectionHead(String accessToken) {
        Section.builder()
                .id('short')
                .title('latest short articles')
                .firstChunk(fetchChunk(accessToken, null, SECTION_SIZE))
                .build()
    }

    @Timed(name = 'fetchChunk')
    SectionChunk fetchChunk(String accessToken, String cursorId, int count) {
        List shortCards = basicCardsService.getUserCards(accessToken, cursorId, count, null, LONGREAD_THRESHOLD)
        return SectionChunk.builder().cards(shortCards).nextCursorId(shortCards.last().id).build()
    }
}
