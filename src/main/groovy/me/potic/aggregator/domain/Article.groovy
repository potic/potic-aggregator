package me.potic.aggregator.domain

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = 'id')
class Article {

    String id
    String pocketId
    String userId

    String givenUrl
    String resolvedUrl
    String title
    boolean read
    boolean favorite
    Integer wordCount
    Collection<String> tags
    Collection<String> authors

    String source

    Image mainImage
    String excerpt

    Long timeAdded
    Long timeUpdated
    Long timeFavored
    Long timeRead

    String content
}
