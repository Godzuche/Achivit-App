package com.godzuche.achivitapp.feature_home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.core.ui.theme.AchivitTypography
import com.godzuche.achivitapp.domain.model.Task

enum class Screen { Profile, Settings, TaskStatusDetail, Category }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
            state = state,
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}

@Composable
fun Home(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(space = 24.dp),
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TaskStatusGrid(
                state = state,
                modifier = Modifier.layout { measurable, constraints ->
                    val placeable: Placeable = measurable.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth + 32.dp.roundToPx()
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeSection(
                title = "Categories",
                viewMoreButtonText = "View All",
            ) {
                CategoriesRow(
                    state = state,
                    modifier = Modifier.layout { measurable, constraints ->
                        val placeable: Placeable = measurable.measure(
                            constraints.copy(
                                maxWidth = constraints.maxWidth + 32.dp.roundToPx()
                            )
                        )
                        layout(placeable.width, placeable.height) {
                            placeable.place(0, 0)
                        }
                    }
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
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
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                fontSize = 18.sp
            )
            if (viewMoreButtonText != null) {
                Text(
                    text = viewMoreButtonText,
                    color = MaterialTheme.colorScheme.primary,
                    style = AchivitTypography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { }
                )
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