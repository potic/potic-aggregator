package me.potic.sections

import me.potic.sections.domain.Card
import me.potic.sections.domain.Section
import me.potic.sections.domain.User

interface SectionFetcher {

    Section section()

    List<Card> fetch(User user, Map fetchCardsRequest)
}