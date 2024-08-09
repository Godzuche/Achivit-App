package com.godzuche.achivitapp.core.domain.repository

import com.godzuche.achivitapp.core.domain.model.RecentSearchQuery
import kotlinx.coroutines.flow.Flow

/**
 * Domain layer interface for abstraction of recent searches repository.
 * */
interface RecentSearchRepository {
    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     * */
    fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>>

    /**
     * Insert the [searchQuery] as part of the recent searches and replace duplicates if available.
     * */
    suspend fun saveOrReplaceRecentSearch(searchQuery: String)

    /**
     * Delete the [query] from recent searches
     * */
    suspend fun deleteRecentSearchQuery(query: String)

    /**
     * Clear the recent searches.
     * */
    suspend fun clearRecentSearches()
}