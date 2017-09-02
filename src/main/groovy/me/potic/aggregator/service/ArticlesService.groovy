package me.potic.aggregator.service

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.aggregator.domain.Article
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

import static com.codahale.metrics.MetricRegistry.name

@Service
@Slf4j
class ArticlesService {

    @Autowired
    HttpBuilder articlesRest

    @Autowired
    MetricRegistry metricRegistry

    Timer retrieveUnreadArticlesOfUserTimer

    Timer retrieveUnreadLongArticlesOfUser

    Timer retrieveUnreadShortArticlesOfUser

    @PostConstruct
    void initMetrics() {
        retrieveUnreadArticlesOfUserTimer = metricRegistry.timer(name('service', 'articles', 'retrieveUnreadArticlesOfUser'))
        retrieveUnreadLongArticlesOfUser = metricRegistry.timer(name('service', 'articles', 'retrieveUnreadLongArticlesOfUser'))
        retrieveUnreadShortArticlesOfUser = metricRegistry.timer(name('service', 'articles', 'retrieveUnreadShortArticlesOfUser'))
    }

    List<Article> retrieveUnreadArticlesOfUser(String accessToken, String cursorId, int count) {
        final Timer.Context timerContext = retrieveUnreadArticlesOfUserTimer.time()
        log.info "requesting $count articles from cursor $cursorId"

        try {
            def query = [count: count]
            if (cursorId != null) {
                query['cursorId'] = cursorId
            }

            return articlesRest.get {
                request.headers['Authorization'] = "Bearer $accessToken".toString()

                request.uri.path = '/user/me/article/unread'
                request.uri.query = query
            }
        } finally {
            long time = timerContext.stop()
            log.info "requesting $count articles from cursor $cursorId took ${time / 1_000_000}ms"
        }
    }

    List<Article> retrieveUnreadLongArticlesOfUser(String accessToken, Integer minLength, String cursorId, int count) {
        final Timer.Context timerContext = retrieveUnreadLongArticlesOfUser.time()
        log.info "requesting $count articles longer than $minLength from cursor $cursorId"

        try {
            def query = [ count: count, minLength: minLength ]
            if (cursorId != null) {
                query['cursorId'] = cursorId
            }

            return articlesRest.get {
                request.headers['Authorization'] = "Bearer $accessToken".toString()

                request.uri.path = '/user/me/article/unread'
                request.uri.query = query
            }
        } finally {
            long time = timerContext.stop()
            log.info "requesting $count articles longer than $minLength from cursor $cursorId took ${time / 1_000_000}ms"
        }
    }

    List<Article> retrieveUnreadShortArticlesOfUser(String accessToken, Integer maxLength, String cursorId, int count) {
        final Timer.Context timerContext = retrieveUnreadShortArticlesOfUser.time()
        log.info "requesting $count articles shorter than $maxLength from cursor $cursorId"

        try {
            def query = [ count: count, maxLength: maxLength ]
            if (cursorId != null) {
                query['cursorId'] = cursorId
            }

            return articlesRest.get {
                request.headers['Authorization'] = "Bearer $accessToken".toString()

                request.uri.path = '/user/me/article/unread'
                request.uri.query = query
            }
        } finally {
            long time = timerContext.stop()
            log.info "requesting $count articles shorter than $maxLength from cursor $cursorId took ${time / 1_000_000}ms"
        }
    }
}
