package me.potic.sections.service

import com.codahale.metrics.annotation.Timed
import groovy.util.logging.Slf4j
import me.potic.sections.domain.Section
import me.potic.sections.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LatestSectionService {

    static final Integer SECTION_SIZE = 5

    @Autowired
    BasicCardsService basicCardsService

    Section fetchSectionHead(String accessToken) {
        Section.builder()
                .id('latest')
                .title('latest articles')
                .firstChunk(fetchChunk(accessToken, null, SECTION_SIZE))
                .build()
    }

    @Timed(name = 'fetchChunk')
    SectionChunk fetchChunk(String accessToken, String cursorId, int count) {
        List latestCards = basicCardsService.getUserCards(accessToken, cursorId, count, null, null)
        return SectionChunk.builder().cards(latestCards).nextCursorId(latestCards.last().id).build()
    }
}
