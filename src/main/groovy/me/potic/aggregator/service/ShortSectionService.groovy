package me.potic.aggregator.service

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.Section
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

import static com.codahale.metrics.MetricRegistry.name

@Service
@Slf4j
class ShortSectionService {

    static final Integer SECTION_SIZE = 5

    static final Integer LONGREAD_THRESHOLD = 500

    @Autowired
    ArticlesService articlesService

    @Autowired
    MetricRegistry metricRegistry

    Timer fetchChunkTimer

    @PostConstruct
    void initMetrics() {
        fetchChunkTimer = metricRegistry.timer(name('service', 'shortSection', 'fetchChunk'))
    }

    Section fetchSectionHead(String accessToken) {
        Section.builder()
                .id('short')
                .title('latest short articles')
                .type('expandable')
                .firstChunk(fetchChunk(accessToken, null, SECTION_SIZE))
                .build()
    }

    SectionChunk fetchChunk(String accessToken, String cursorId, int count) {
        final Timer.Context timerContext = fetchChunkTimer.time()
        try {
            List shortArticles = articlesService.retrieveUnreadShortArticlesOfUser(accessToken, LONGREAD_THRESHOLD, cursorId, count)
            return SectionChunk.builder().articles(shortArticles).nextCursorId(shortArticles.last().id).build()
        } finally {
            timerContext.stop()
        }
    }
}
