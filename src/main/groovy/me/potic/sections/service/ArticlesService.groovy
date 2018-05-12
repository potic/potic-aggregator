package me.potic.sections.service

import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder
import me.potic.sections.domain.Article
import me.potic.sections.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class ArticlesService {

    HttpBuilder articlesServiceRest

    @Autowired
    HttpBuilder articlesServiceRest(@Value('${services.articles.url}') String articlesServiceUrl) {
        articlesServiceRest = HttpBuilder.configure {
            request.uri = articlesServiceUrl
        }
    }

    List<Article> getLatestUnreadArticles(User user, List<String> skipIds, Integer count) {
        getLatestUnreadArticles(user, skipIds, count, null, null)
    }

    List<Article> getLatestUnreadArticles(User user, List<String> skipIds, Integer count, Integer minLength, Integer maxLength) {
        log.debug "getting $count latest unread articles with min length ${minLength} and max length ${maxLength} for user ${user.id} with skipIds=${skipIds}"

        try {
            String params = "userId: \"${user.id}\""
            if (skipIds != null) {
                params += ", skipIds: ${skipIds.collect({ '"' + it + '"' })}"
            }
            if (count != null) {
                params += ", count: ${count}"
            }
            if (minLength != null) {
                params += ", minLength: ${minLength}"
            }
            if (maxLength != null) {
                params += ", maxLength: ${maxLength}"
            }

            def response = articlesServiceRest.post {
                request.uri.path = '/graphql'
                request.contentType = 'application/json'
                request.body = [ query: """
                    {
                      latestUnread(${params}) {
                        id
                        
                        card {
                            id
                            timestamp
                            pocketId
                            url
                            title
                            source
                            excerpt
                            addedTimestamp
                            image {
                                src
                            }
                        }
                      }
                    }
                """ ]
            }

            List errors = response.errors
            if (errors != null && !errors.empty) {
                throw new RuntimeException("Request failed: $errors")
            }

            return response.data.latestUnread.collect({ new Article(it) })
        } catch (e) {
            log.error "getting $count unread articles with min length ${minLength} and max length ${maxLength} for user ${user.id} with skipIds=${skipIds} failed: $e.message", e
            throw new RuntimeException("getting $count unread articles with min length ${minLength} and max length ${maxLength} for user ${user.id} with skipIds=${skipIds} failed: $e.message", e)
        }
    }

    List<Article> getRankedUnreadArticles(User user, String rankId, List<String> skipIds, Integer count) {
        log.debug "getting $count ranked by ${rankId} unread articles for user ${user.id} with skipIds=${skipIds}"

        try {
            String params = "userId: \"${user.id}\""
            if (rankId != null) {
                params += ", rankId: \"${rankId}\""
            }
            if (skipIds != null) {
                params += ", skipIds: ${skipIds.collect({ '"' + it + '"' })}"
            }
            if (count != null) {
                params += ", count: ${count}"
            }

            def response = articlesServiceRest.post {
                request.uri.path = '/graphql'
                request.contentType = 'application/json'
                request.body = [ query: """
                    {
                      rankedUnread(${params}) {
                        id
                        
                        card {
                            id
                            timestamp
                            pocketId
                            url
                            title
                            source
                            excerpt
                            addedTimestamp
                            image {
                                src
                            }
                        }
                      }
                    }
                """ ]
            }

            List errors = response.errors
            if (errors != null && !errors.empty) {
                throw new RuntimeException("Request failed: $errors")
            }

            return response.data.rankedUnread.collect({ new Article(it) })
        } catch (e) {
            log.error "getting $count ranked by ${rankId} unread articles for user ${user.id} with skipIds=${skipIds} failed: $e.message", e
            throw new RuntimeException("getting $count ranked by ${rankId} unread articles for user ${user.id} with skipIds=${skipIds} failed: $e.message", e)
        }
    }

    List<Article> getRandomUnreadArticles(User user, List<String> skipIds, Integer count) {
        log.debug "getting $count random unread articles for user ${user.id} with skipIds=${skipIds}"

        try {
            String params = "userId: \"${user.id}\""
            if (skipIds != null) {
                params += ", skipIds: ${skipIds.collect({ '"' + it + '"' })}"
            }
            if (count != null) {
                params += ", count: ${count}"
            }

            def response = articlesServiceRest.post {
                request.uri.path = '/graphql'
                request.contentType = 'application/json'
                request.body = [ query: """
                    {
                      randomUnread(${params}) {
                        id
                        
                        card {
                            id
                            timestamp
                            pocketId
                            url
                            title
                            source
                            excerpt
                            addedTimestamp
                            image {
                                src
                            }
                        }
                      }
                    }
                """ ]
            }

            List errors = response.errors
            if (errors != null && !errors.empty) {
                throw new RuntimeException("Request failed: $errors")
            }

            return response.data.randomUnread.collect({ new Article(it) })
        } catch (e) {
            log.error "getting $count random unread articles for user ${user.id} with skipIds=${skipIds} failed: $e.message", e
            throw new RuntimeException("getting $count random unread articles for user ${user.id} with skipIds=${skipIds} failed: $e.message", e)
        }
    }
}
