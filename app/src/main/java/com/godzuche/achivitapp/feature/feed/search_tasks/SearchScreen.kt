package com.godzuche.achivitapp.feature.feed.search_tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.godzuche.achivitapp.core.design_system.components.SearchToolbar
import com.godzuche.achivitapp.feature.feed.task_list.TasksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun SearchRoute(
    onBackClick: () -> Unit,
    tasksViewModel: TasksViewModel,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    var query by remember {
        mutableStateOf("")
    }

    val tasks = tasksViewModel.tasksPagingDataFlow.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {}
    ) {
        SearchScreen(
            searchQuery = query,
            onSearchQueryChanged = {
                query = it
            },
            onSearchTriggered = {
                keyboardController?.hide()
            },
            onBackClick = onBackClick,
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
        )
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchToolbar(
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchTriggered = onSearchTriggered,
            onBackClick = onBackClick,
            content = {
                //
            }
        )
    }
}