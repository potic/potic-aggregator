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

    List<Article> retrieveUnreadArticlesOfUser(String accessToken, String cursorId, int count) {
        log.info "requesting $count articles from cursor $cursorId"

        def query = [ count: count ]
        if (cursorId != null) {
            query['cursorId'] = cursorId
        }

        articlesRest.get {
            request.headers['Authorization'] = "Bearer $accessToken".toString()

            request.uri.path = '/user/me/article/unread'
            request.uri.query = query
        }
    }

    List<Article> retrieveUnreadLongArticlesOfUser(String accessToken, Integer minLength, String cursorId, int count) {
        log.info "requesting $count articles longer than $minLength from cursor $cursorId"

        def query = [ count: count, minLength: minLength ]
        if (cursorId != null) {
            query['cursorId'] = cursorId
        }

        articlesRest.get {
            request.headers['Authorization'] = "Bearer $accessToken".toString()

            request.uri.path = '/user/me/article/unread'
            request.uri.query = query
        }
    }

    List<Article> retrieveUnreadShortArticlesOfUser(String accessToken, Integer maxLength, String cursorId, int count) {
        log.info "requesting $count articles longer than $maxLength from cursor $cursorId"

        def query = [ count: count, maxLength: maxLength ]
        if (cursorId != null) {
            query['cursorId'] = cursorId
        }

        articlesRest.get {
            request.headers['Authorization'] = "Bearer $accessToken".toString()

            request.uri.path = '/user/me/article/unread'
            request.uri.query = query
        }
    }
}
