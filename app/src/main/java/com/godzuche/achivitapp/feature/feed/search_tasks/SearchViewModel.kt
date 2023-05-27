package com.godzuche.achivitapp.feature.feed.search_tasks

import androidx.lifecycle.ViewModel
import com.godzuche.achivitapp.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState: MutableStateFlow<SearchUiState> =
        MutableStateFlow(SearchUiState.Success())
}

sealed class SearchUiState {
    private val query: String = ""

    object Loading : SearchUiState()
    data class Success(
        val searchResult: List<Task> = emptyList()
    ) : SearchUiState()
}