package me.potic.sections.service

import groovy.util.logging.Slf4j
import me.potic.sections.SectionFetcher
import me.potic.sections.domain.Article
import me.potic.sections.domain.Model
import me.potic.sections.domain.Section
import me.potic.sections.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class RecommendedArticlesSection implements SectionFetcher {

    static final Section SECTION = new Section(id: 'recommended', title: 'recommended', priority: 1)

    @Autowired
    ArticlesService articlesService

    @Autowired
    ModelsService modelsService

    @Override
    Section section() {
        SECTION
    }

    @Override
    List<Article> fetch(User user, Map fetchCardsRequest) {
        log.debug "fetching cards with recommended articles for user ${user} with request ${fetchCardsRequest}"

        try {
            List<String> skipIds = fetchCardsRequest.skipIds
            Integer count = fetchCardsRequest.count

            Model actualModel = modelsService.getActualModel()

            return articlesService.getRankedUnreadArticles(user, "${actualModel.name}:${actualModel.version}", skipIds, count)
        } catch (e) {
            log.error "fetching cards with recommended articles for user ${user} with request ${fetchCardsRequest} failed: $e.message", e
            throw new RuntimeException("fetching cards with recommended articles for user ${user} with request ${fetchCardsRequest} failed", e)
        }
    }
}
