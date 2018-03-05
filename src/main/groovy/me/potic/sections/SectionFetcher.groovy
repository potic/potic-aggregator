package me.potic.sections

import me.potic.sections.domain.Article
import me.potic.sections.domain.Section
import me.potic.sections.domain.User

interface SectionFetcher {

    Section section()

    List<Article> fetch(User user, Map fetchCardsRequest)
}