package me.potic.sections

import me.potic.sections.domain.Section
import me.potic.sections.domain.SectionChunk

interface SectionFetcher {

    Section section()

    SectionChunk fetch()
}