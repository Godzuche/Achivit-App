package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.data.local.database.dao.RecentSearchQueryDao
import com.godzuche.achivitapp.data.local.database.model.RecentSearchQueryEntity
import com.godzuche.achivitapp.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.domain.model.RecentSearchQuery
import com.godzuche.achivitapp.domain.repository.RecentSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import javax.inject.Inject

class DefaultRecentSearchRepository @Inject constructor(
    private val recentSearchQueryDao: RecentSearchQueryDao,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : RecentSearchRepository {
    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        recentSearchQueryDao.getRecentSearchQueryEntities(limit = limit)
            .map { it.map(RecentSearchQueryEntity::asExternalModel) }

    override suspend fun saveOrReplaceRecentSearch(searchQuery: String) {
        withContext(ioDispatcher) {
            recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
                recentSearchQuery = RecentSearchQueryEntity(
                    query = searchQuery,
                    queriedDate = Clock.System.now()
                )
            )
        }
    }

    override suspend fun deleteRecentSearchQuery(qery: String) =
        recentSearchQueryDao.deleteQuery(qery)

    override suspend fun clearRecentSearches() = recentSearchQueryDao.clearRecentSearchQueries()
}