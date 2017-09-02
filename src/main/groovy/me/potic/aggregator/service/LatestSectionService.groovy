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
class LatestSectionService {

    static final Integer SECTION_SIZE = 5

    @Autowired
    ArticlesService articlesService

    @Autowired
    MetricRegistry metricRegistry

    Timer fetchChunkTimer

    @PostConstruct
    void initMetrics() {
        fetchChunkTimer = metricRegistry.timer(name('service', 'latestSection', 'fetchChunk'))
    }

    Section fetchSectionHead(String accessToken) {
        Section.builder()
                .id('latest')
                .title('latest articles')
                .type('expandable')
                .firstChunk(fetchChunk(accessToken, null, SECTION_SIZE))
                .build()
    }

    SectionChunk fetchChunk(String accessToken, String cursorId, int count) {
        final Timer.Context timerContext = fetchChunkTimer.time()
        try {
            List latestArticles = articlesService.retrieveUnreadArticlesOfUser(accessToken, cursorId, count)
            return SectionChunk.builder().articles(latestArticles).nextCursorId(latestArticles.last().id).build()
        } finally {
            timerContext.stop()
        }
    }
}
