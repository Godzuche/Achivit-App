package com.godzuche.achivitapp.presentation.tasks.search_tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.SearchToolbar
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.presentation.tasks.task_list.TaskCard
import com.godzuche.achivitapp.presentation.tasks.task_list.TasksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun SearchRoute(
    tasksViewModel: TasksViewModel,
    onBackClick: () -> Unit,
    onExploreTasksClick: () -> Unit,
    onTaskClick: (Int) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val recentSearchQueriesUiState by searchViewModel.recentSearchQueriesUiState.collectAsStateWithLifecycle()
    val searchResultUiState by searchViewModel.searchResultUiState.collectAsStateWithLifecycle()
    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current

    SearchScreen(
        searchQuery = searchQuery,
        searchResultUiState = searchResultUiState,
        recentSearchesUiState = recentSearchQueriesUiState,
        onSearchQueryChanged = searchViewModel::onSearchQueryChanged,
        onSearchTriggered = { query ->
            keyboardController?.hide()
            searchViewModel.onSearchTriggered(query)
        },
        onDeleteRecentQuery = searchViewModel::onDeleteRecentQuery,
        onBackClick = onBackClick,
        onExploreTasksClick = onExploreTasksClick,
        onTaskClick = onTaskClick,
        onClearRecentSearches = searchViewModel::clearRecentSearches
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    searchResultUiState: SearchResultUiState,
    recentSearchesUiState: RecentSearchQueriesUiState,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    onExploreTasksClick: () -> Unit,
    onClearRecentSearches: () -> Unit,
    onDeleteRecentQuery: (String) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
        ) {
            SearchToolbar(
                searchQuery = searchQuery,
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchTriggered = onSearchTriggered,
                onBackClick = onBackClick,
                content = {
                    when (searchResultUiState) {
                        SearchResultUiState.Loading,
                        SearchResultUiState.LoadingFailed
                        -> Unit

                        SearchResultUiState.SearchNotReady -> SearchNotReadyBody()
                        SearchResultUiState.EmptyQuery -> {
                            if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                                RecentSearchesBody(
                                    onClearRecentSearches = onClearRecentSearches,
                                    onRecentSearchClicked = {
                                        onSearchQueryChanged(it)
                                        onSearchTriggered(it)
                                    },
                                    recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                                    onDeleteRecentQuery = onDeleteRecentQuery
                                )
                            }
                        }

                        is SearchResultUiState.Success -> {
                            if (searchResultUiState.isEmpty()) {
                                EmptySearchResultBody(
                                    searchQuery = searchQuery,
                                    onExploreTasksClick = onExploreTasksClick
                                )
                                if (recentSearchesUiState is RecentSearchQueriesUiState.Success) {
                                    RecentSearchesBody(
                                        onClearRecentSearches = onClearRecentSearches,
                                        onRecentSearchClicked = {
                                            onSearchQueryChanged(it)
                                            onSearchTriggered(it)
                                        },
                                        recentSearchQueries = recentSearchesUiState.recentQueries.map { it.query },
                                        onDeleteRecentQuery = onDeleteRecentQuery
                                    )
                                }
                            } else {
                                SearchResultBody(
                                    tasks = searchResultUiState.tasks,
                                    searchQuery = searchQuery,
                                    onSearchTriggered = onSearchTriggered,
                                    onTaskClick = onTaskClick
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun EmptySearchResultBody(
    searchQuery: String,
    onExploreTasksClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        val message = stringResource(id = R.string.search_result_not_found, searchQuery)
        val start = message.indexOf(searchQuery)
        Text(
            text = AnnotatedString(
                text = message,
                spanStyles = listOf(
                    AnnotatedString.Range(
                        SpanStyle(fontWeight = FontWeight.Bold),
                        start = start,
                        end = start + searchQuery.length,
                    ),
                ),
            ),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
        val tasks = stringResource(id = R.string.tasks)
        val tryAnotherSearchString = buildAnnotatedString {
            append(stringResource(id = R.string.try_another_search))
            append(" ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                pushStringAnnotation(tag = tasks, annotation = tasks)
                append(tasks)
            }
            append(" ")
            append("to browse topics")
        }
        ClickableText(
            text = tryAnotherSearchString,
            style = MaterialTheme.typography.bodyLarge.merge(
                TextStyle(textAlign = TextAlign.Center)
            ),
            modifier = Modifier.padding(start = 36.dp, end = 36.dp, bottom = 24.dp),
            onClick = { offset ->
                tryAnotherSearchString.getStringAnnotations(
                    tag = tasks,
                    start = offset,
                    end = offset
                )
                    .firstOrNull()
                    .let {
                        onExploreTasksClick()
                    }
            }
        )
    }
}

@Composable
private fun SearchNotReadyBody() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 48.dp),
    ) {
        Text(
            text = stringResource(id = R.string.search_not_ready),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp),
        )
    }
}

@Composable
fun RecentSearchesBody(
    onClearRecentSearches: () -> Unit,
    onDeleteRecentQuery: (String) -> Unit,
    onRecentSearchClicked: (String) -> Unit,
    recentSearchQueries: List<String>
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Recent searches",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (recentSearchQueries.isNotEmpty()) {
                TextButton(
                    onClick = onClearRecentSearches,
                ) {
                    Text(stringResource(R.string.clear_all))
                }
            }
        }
        LazyColumn {
            items(items = recentSearchQueries) { recentSearch ->
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AchivitIcons.History,
                            contentDescription = null
                        )
                        Text(
                            text = recentSearch,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .clickable {
                                    onRecentSearchClicked(recentSearch)
                                }
                                .weight(1f)
                        )
                        IconButton(
                            onClick = { onDeleteRecentQuery(recentSearch) }
                        ) {
                            Icon(
                                imageVector = AchivitIcons.Close,
                                contentDescription = stringResource(R.string.clear_recent_search_query)
                            )
                        }
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultBody(
    searchQuery: String,
    tasks: List<Task>,
    onSearchTriggered: (String) -> Unit,
    onTaskClick: (Int) -> Unit
) {
    val state = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.fillMaxSize(),
        state = state
    ) {
        if (tasks.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Tasks")
                            append(" ")
                            append("(${tasks.size})")
                        }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            tasks.forEach { task ->
                // Append the task id to make each key unique
                val taskId = task.id
                item(
                    key = "task-$taskId",
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    TaskCard(
                        task = task,
                        onDoneCheck = { _, _ -> },
                        onTaskClick = {
                            // Pass the current search query to ViewModel to save it as recent searches
                            onSearchTriggered(searchQuery)
                            onTaskClick(taskId!!)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecentSearchBodyPreview() {
    RecentSearchesBody(
        onClearRecentSearches = {},
        onRecentSearchClicked = {},
        recentSearchQueries = listOf("Work", "School", "Social media"),
        onDeleteRecentQuery = {}
    )
}