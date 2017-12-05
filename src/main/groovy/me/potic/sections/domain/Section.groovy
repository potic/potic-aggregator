package me.potic.sections.domain

import groovy.transform.builder.Builder

@Builder
class Section {

    String id

    String title

    SectionChunk firstChunk
}
