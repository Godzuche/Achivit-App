package com.godzuche.achivitapp.feature.tasks.search_tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.common.asResult
import com.godzuche.achivitapp.domain.repository.RecentSearchRepository
import com.godzuche.achivitapp.domain.usecase.GetRecentSearchQueriesUseCase
import com.godzuche.achivitapp.domain.usecase.GetSearchContentsCountUseCase
import com.godzuche.achivitapp.domain.usecase.GetSearchContentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    recentSearchQueriesUseCase: GetRecentSearchQueriesUseCase,
    getSearchContentsCountUseCase: GetSearchContentsCountUseCase,
    getSearchContentsUseCase: GetSearchContentsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {
    val searchQuery = savedStateHandle.getStateFlow(SEARCH_QUERY, "")

    val searchResultUiState: StateFlow<SearchResultUiState> =
        getSearchContentsCountUseCase().flatMapLatest { totalCount ->
            if (totalCount < SEARCH_MIN_FTS_ENTITY_COUNT) {
                flowOf(SearchResultUiState.SearchNotReady)
            } else {
                searchQuery.flatMapLatest { query ->
                    if (query.length < SEARCH_QUERY_MIN_LENGTH) {
                        flowOf(SearchResultUiState.EmptyQuery)
                    } else {
                        getSearchContentsUseCase(query).asResult().map {
                            when (it) {
                                is AchivitResult.Success -> {
                                    SearchResultUiState.Success(tasks = it.data.tasks)
                                }

                                is AchivitResult.Loading -> {
                                    SearchResultUiState.Loading
                                }

                                is AchivitResult.Error -> {
                                    SearchResultUiState.LoadingFailed
                                }
                            }
                        }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchResultUiState.Loading
        )

    val recentSearchQueriesUiState: StateFlow<RecentSearchQueriesUiState> =
        recentSearchQueriesUseCase().map(RecentSearchQueriesUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecentSearchQueriesUiState.Loading
            )

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[SEARCH_QUERY] = query
    }

    /**
     * Called when the search action is explicitly triggered by the user
     * when the search icon is tapped in the IME or when the enter key is pressed in the search text field.
     * */
    fun onSearchTriggered(query: String) {
        viewModelScope.launch {
            recentSearchRepository.saveOrReplaceRecentSearch(query)
        }
    }

    fun onDeleteRecentQuery(query: String) {
        viewModelScope.launch {
            recentSearchRepository.deleteRecentSearchQuery(query)
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.clearRecentSearches()
        }
    }

}

private const val SEARCH_QUERY = "search_query"

/** Minimum length below which search query is considered [SearchResultUiState.EmptyQuery]*/
private const val SEARCH_QUERY_MIN_LENGTH = 2

/** Minimum count of the search fts entity below which it is considered as [SearchResultUiState.SearchNotReady]*/
private const val SEARCH_MIN_FTS_ENTITY_COUNT = 1