package com.godzuche.achivitapp.feature.tasks.search_tasks

import com.godzuche.achivitapp.domain.model.Task

sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState
    data object LoadingFailed : SearchResultUiState
    data class Success(
        val tasks: List<Task>
        //Todo: include collections and categories that match the search query
    ) : SearchResultUiState {
        fun isEmpty(): Boolean = tasks.isEmpty()
    }

    /**
     * A state where the search contents are not ready. This happens when the *Fts tables are not
     * populated yet.
     */
    data object SearchNotReady : SearchResultUiState
}