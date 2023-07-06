package com.godzuche.achivitapp.presentation.tasks.search_tasks

import com.godzuche.achivitapp.domain.model.RecentSearchQuery

sealed interface RecentSearchQueriesUiState {
    object Loading : RecentSearchQueriesUiState

    data class Success(
        val recentQueries: List<RecentSearchQuery> = emptyList()
    ) : RecentSearchQueriesUiState
}