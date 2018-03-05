package me.potic.sections.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.sections.domain.Article
import me.potic.sections.domain.ArticleEvent
import me.potic.sections.domain.ArticleEventType
import me.potic.sections.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
@Slf4j
class FeedbackService {

    HttpBuilder feedbackServiceRest

    @Autowired
    HttpBuilder feedbackServiceRest(@Value('${services.feedback.url}') String feedbackServiceUrl) {
        feedbackServiceRest = HttpBuilder.configure {
            request.uri = feedbackServiceUrl
        }
    }

    void showed(User user, Article article) {
        log.debug "emitting SHOWED event for user ${user} and article ${article}..."

        try {
            ArticleEvent articleEvent = new ArticleEvent()
            articleEvent.type = ArticleEventType.SHOWED
            articleEvent.articleId = article.id
            articleEvent.userId = user.id
            articleEvent.timestamp = LocalDateTime.now().toString()

            feedbackServiceRest.post {
                request.uri.path = "/event"
                request.body = articleEvent
                request.contentType = 'application/json'
            }
        } catch (e) {
            log.error "emitting SHOWED event for user ${user} and article ${article} failed: $e.message", e
            throw new RuntimeException("emitting SHOWED event for user ${user} and article ${article} failed: $e.message", e)
        }
    }
}
