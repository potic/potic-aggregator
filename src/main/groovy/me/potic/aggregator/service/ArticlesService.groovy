package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Article
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ArticlesService {

    @Autowired
    HttpBuilder articlesRest

    List<Article> retrieveUnreadArticlesOfUser(String userId, int page, int size) {
        log.info "requesting $size articles from page #$page"

        articlesRest.get {
            request.uri.path = "/article/byUserId/${userId}/unread"
            request.uri.query = [ page: page, size: size ]
        }
    }
}
