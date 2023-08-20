package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.core.design_system.components.HomeTopAppBar
import com.godzuche.achivitapp.core.design_system.components.OnlineStatusIndicator
import com.godzuche.achivitapp.core.design_system.theme.AchivitTypography
import com.godzuche.achivitapp.core.design_system.theme.Alpha
import com.godzuche.achivitapp.core.ui.util.removeWidthConstraint
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.model.UserData
import com.godzuche.achivitapp.feature.auth.AuthViewModel
import com.godzuche.achivitapp.feature.auth.UserAuthState

enum class HomeTopBarActions {
    PROFILE,
    SETTINGS
}

@Composable
fun HomeRoute(
    onTopBarAction: (HomeTopBarActions) -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val userAuthState by authViewModel.userAuthState.collectAsStateWithLifecycle()
    val isOffline by homeViewModel.isOffline.collectAsStateWithLifecycle()

    HomeScreen(
        userAuthState = userAuthState,
        homeUiState = homeUiState,
        onTodayTaskClick = onNavigateToTaskDetail,
        modifier = modifier,
        onTopBarAction = onTopBarAction,
        isOnline = isOffline.not(),
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    userAuthState: UserAuthState,
    homeUiState: HomeUiState,
    onTodayTaskClick: (Int) -> Unit,
    onTopBarAction: (HomeTopBarActions) -> Unit,
    modifier: Modifier = Modifier,
    isOnline: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    val listState = rememberLazyGridState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopAppBar(
                userAuthState = userAuthState,
                isOnline = isOnline,
                todayTasks = homeUiState.todayTasks.size,
                scrollBehavior = scrollBehavior,
                onProfileIconClicked = {
                    onTopBarAction(HomeTopBarActions.PROFILE)
                },
                onSettingsActionClicked = {
                    onTopBarAction(HomeTopBarActions.SETTINGS)
                },
                onTopBarTitleClicked = {
                    onTopBarAction(HomeTopBarActions.PROFILE)
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .then(modifier)
        ) {
            val onlineText = if (isOnline) "Online" else "Offline"

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    OnlineStatusIndicator(isOnline = isOnline)
                    Text(
                        text = onlineText,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(Alpha.medium),
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 24.dp),
                modifier = Modifier/*
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)*/
                    .fillMaxSize()
//                    .then(modifier)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TaskStatusGrid(
                        state = homeUiState,
                        modifier = Modifier.removeWidthConstraint(contentPadding = 16.dp)
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HomeSection(
                        title = "Categories",
                        viewMoreButtonText = "View All",
                    ) {
                        CategoriesRow(
                            state = homeUiState,
                            modifier = Modifier.removeWidthConstraint(contentPadding = 16.dp)
                        )
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HomeSection(
                        title = "Today's tasks",
                        viewMoreButtonText = "View All"
                    ) {
                        TodayTasks(
                            homeUiState = homeUiState,
                            onTaskClick = onTodayTaskClick,
                            modifier = Modifier.removeWidthConstraint(16.dp)
                        )
                    }
                }
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
            userAuthState = UserAuthState.SignedIn(
                data = UserData(
                    userId = "",
                    displayName = null,
                    email = null,
                    profilePictureUrl = null,
                    createdDate = 0L
                )
            ),
            homeUiState = HomeUiState(
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
            onTopBarAction = {},
            onTodayTaskClick = {},
            isOnline = false,
        )
    }
}