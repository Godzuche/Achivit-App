package com.godzuche.achivitapp.feature.tasks.search_tasks

import com.godzuche.achivitapp.core.domain.model.RecentSearchQuery

sealed interface RecentSearchQueriesUiState {
    object Loading : RecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<RecentSearchQuery> = emptyList()
    ) : RecentSearchQueriesUiState
}