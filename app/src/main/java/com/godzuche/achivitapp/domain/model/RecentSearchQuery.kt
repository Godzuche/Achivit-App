package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.database.model.RecentSearchQueryEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class RecentSearchQuery(
    val query: String,
    val queryDate: Instant = Clock.System.now()
)

fun RecentSearchQueryEntity.asExternalModel() = RecentSearchQuery(
    query = query,
    queryDate = queriedDate
)