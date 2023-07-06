package com.godzuche.achivitapp.data.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.godzuche.achivitapp.data.local.database.model.RecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchQueryDao {
    @Query(value = "SELECT * FROM recent_search_queries ORDER BY query_date DESC LIMIT :limit")
    fun getRecentSearchQueryEntities(limit: Int): Flow<List<RecentSearchQueryEntity>>

    @Upsert
    suspend fun insertOrReplaceRecentSearchQuery(recentSearchQuery: RecentSearchQueryEntity)

    @Query(
        """
            DELETE FROM recent_search_queries
            WHERE `query` = :query
        """
    )
    suspend fun deleteQuery(query: String)

    @Query(value = "DELETE FROM recent_search_queries")
    suspend fun clearRecentSearchQueries()
}