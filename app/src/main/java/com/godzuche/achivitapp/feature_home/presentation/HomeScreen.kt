package com.godzuche.achivitapp.feature_home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.core.domain.model.Task

enum class Screen { Profile, Settings, TaskStatusDetail, Category }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                todayTasks = state.todayTasks.size,
                scrollBehavior = scrollBehavior,
                onProfileIconClicked = {
                    onEvent(HomeUiEvent.Navigate(Screen.Profile))
                },
                onSettingsActionClicked = {
                    onEvent(HomeUiEvent.Navigate(Screen.Settings))
                },
                onTopBarTitleClicked = {
                    onEvent(HomeUiEvent.Navigate(Screen.Profile))
                }
            )
        }
    ) { innerPadding ->
        Home(
            innerPadding = innerPadding,
            state = state,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Home(
    innerPadding: PaddingValues,
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .consumedWindowInsets(innerPadding)
            .then(modifier),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        state = listState
    ) {
        item {
            TaskStatusGrid(
                state = state,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        item {
            HomeSection(
                title = "Categories",
                viewMoreButtonText = "View All",
//                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                CategoriesRow(state = state)
            }
        }
        item {
            HomeSection(
                title = "Today's tasks",
                modifier = Modifier.padding(horizontal = 8.dp),
                viewMoreButtonText = "View All"
            ) {
                TodayTasks(state = state)
            }
        }
    }
}

@Composable
fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    viewMoreButtonText: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                fontSize = 18.sp
            )
            if (viewMoreButtonText != null) {
                TextButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(text = viewMoreButtonText)
                }
            }
        }
        content()
    }
}

@Preview
@Composable
fun HomeSectionPreview() {
    Surface {
        HomeSection(title = "Categories", viewMoreButtonText = "View All") {
            CategoriesRow(state = HomeUiState())
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    Surface {
        HomeScreen(
            state = HomeUiState(
                todayTasks = listOf(
                    Task(
                        id = 0,
                        title = "Lights out",
                        created = 0L,
                        description = "Go to bed",
                        dueDate = 0L,
                        collectionTitle = "Personal Collection",
                        categoryTitle = "Demo"
                    )
                )
            ),
            onEvent = {}
        )
    }
}