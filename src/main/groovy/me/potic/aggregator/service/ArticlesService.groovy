package me.potic.aggregator.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Article
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class ArticlesService {

    static final String SANDBOX_USER_ID = '58b1800dc9e77c0001d1d702'

    @Autowired
    HttpBuilder articlesRest

    List<Article> unreadForSandboxUser(int page, int size) {
        log.info "requesting $size articles from page #$page"

        articlesRest.get {
            request.uri.path = "/article/byUserId/${SANDBOX_USER_ID}/unread"
            request.uri.query = [ page: page, size: size ]
        }
    }
}
