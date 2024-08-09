package com.godzuche.achivitapp.core.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class RecentSearchQuery(
    val query: String,
    val queryDate: Instant = Clock.System.now()
)