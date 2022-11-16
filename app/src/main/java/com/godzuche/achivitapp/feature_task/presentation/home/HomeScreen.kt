package com.godzuche.achivitapp.feature_task.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp

enum class Screen { Profile, Settings, TaskStatusDetail, Category }

sealed class HomeEvent {
    data class Navigate(val screen: Screen) : HomeEvent()
    object NavigateBack : HomeEvent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onEvent: (HomeEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    Surface {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onProfileIconClicked = {
                        onEvent(HomeEvent.Navigate(Screen.Profile))
                    },
                    onSettingsActionClicked = {
                        onEvent(HomeEvent.Navigate(Screen.Settings))
                    },
                    onTopBarTitleClicked = {
                        onEvent(HomeEvent.Navigate(Screen.Profile))
                    }
                )
            }
        ) { innerPadding ->
            Home(innerPadding = innerPadding, homeViewModel = homeViewModel)
        }
    }
}

@Composable
fun HomeSection(
    modifier: Modifier = Modifier,
    title: String,
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