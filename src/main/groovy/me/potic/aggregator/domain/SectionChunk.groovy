package me.potic.aggregator.domain

import groovy.transform.builder.Builder

@Builder
class SectionChunk {

    List<Article> articles

    String nextCursorId
}
