package com.godzuche.achivitapp.core.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.core.domain.model.RecentSearchQuery
import kotlinx.datetime.Instant

/**
 * Defines an database entity that stores recent search queries.
 * */
@Entity(
    tableName = "recent_search_queries"
)
data class RecentSearchQueryEntity(
    @PrimaryKey
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "query_date")
    val queriedDate: Instant
)

fun RecentSearchQueryEntity.asExternalModel() = RecentSearchQuery(
    query = query,
    queryDate = queriedDate
)