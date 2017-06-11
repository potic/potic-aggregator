package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import me.potic.aggregator.domain.SectionChunk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class LatestSectionService {

    static final Integer SECTION_SIZE = 5

    @Autowired
    ArticlesService articlesService

    SectionChunk fetchChunkById(String id) {
        Integer page = Integer.parseInt(id)

        List latestArticles = articlesService.unreadForSandboxUser(page, SECTION_SIZE)

        SectionChunk.builder().id(id).articles(latestArticles).nextChunkId("${page + 1}").build()
    }
}
