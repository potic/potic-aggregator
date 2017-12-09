package me.potic.sections.service

import groovy.util.logging.Slf4j
import me.potic.sections.SectionFetcher
import me.potic.sections.domain.Article
import me.potic.sections.domain.Card
import me.potic.sections.domain.Section
import me.potic.sections.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ShortArticlesSectionFetcher implements SectionFetcher {

    static final Section SECTION = new Section(id: 'short', title: 'short articles', priority: 4)

    static final Integer MAX_LENGTH_OF_SHORT_ARTICLE = 300

    @Autowired
    ArticlesService articlesService

    @Override
    Section section() {
        SECTION
    }

    @Override
    List<Card> fetch(User user, Map fetchCardsRequest) {
        log.debug "fetching cards with short articles for user ${user} with request ${fetchCardsRequest}"

        try {
            List<String> skipIds = fetchCardsRequest.skipIds
            Integer count = fetchCardsRequest.count
            List<Article> articles = articlesService.getLatestUnreadArticles(user, skipIds, count, null, MAX_LENGTH_OF_SHORT_ARTICLE)
            return articles*.card
        } catch (e) {
            log.error "fetching cards with short articles for user ${user} with request ${fetchCardsRequest} failed: $e.message", e
            throw new RuntimeException("fetching cards with short articles for user ${user} with request ${fetchCardsRequest} failed", e)
        }
    }
}