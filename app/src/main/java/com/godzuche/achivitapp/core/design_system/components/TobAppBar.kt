package com.godzuche.achivitapp.core.design_system.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    todayTasks: Int = 0,
    name: String = "Jonathan",
    scrollBehavior: TopAppBarScrollBehavior,
    onSettingsActionClicked: () -> Unit,
    onProfileIconClicked: () -> Unit,
    onTopBarTitleClicked: () -> Unit
) {
    val ctx = LocalContext.current
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Column(
                modifier = Modifier.clickable(enabled = true, onClick = onTopBarTitleClicked),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ctx.resources.getString(R.string.greeting, name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pluralStringResource(
                        id = R.plurals.activeTasksMessage,
                        count = todayTasks,
                        formatArgs = arrayOf(todayTasks)
                    ),
                    fontSize = 14.sp
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onProfileIconClicked,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsActionClicked) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onBackClick: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
    modifier: Modifier = Modifier
) {
    var active by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = AchivitIcons.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.back
                    )
                )
            }
        }
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChanged,
            onSearch = onSearchTriggered,
            active = active,
            onActiveChange = { isActive ->
                active = isActive
            },
            leadingIcon = {
                Icon(
                    imageVector = AchivitIcons.Search,
                    contentDescription = stringResource(
                        id = R.string.search
                    )
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotEmpty(),
                    enter = fadeIn() + slideInHorizontally(
                        animationSpec = tween(),
                        initialOffsetX = {
                            it
                        }
                    ),
                    exit = fadeOut() + slideOutHorizontally(
                        animationSpec = tween(),
                        targetOffsetX = {
                            it
                        }
                    )
                ) {
                    IconButton(
                        onClick = {
                            onSearchQueryChanged("")
                        }
                    ) {
                        Icon(
                            imageVector = AchivitIcons.Close,
                            contentDescription = stringResource(R.string.clear_search_text_content_desc)
                        )
                    }
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0),
            placeholder = {
                Text(text = "Search tasks")
            },
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecircuTopBar(
    title: @Composable () -> Unit
) {
    TopAppBar(
        title = title
    )
}