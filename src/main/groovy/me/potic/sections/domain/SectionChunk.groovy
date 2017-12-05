package me.potic.sections.domain

import groovy.transform.builder.Builder

@Builder
class SectionChunk {

    List<Card> cards

    String nextCursorId
}
