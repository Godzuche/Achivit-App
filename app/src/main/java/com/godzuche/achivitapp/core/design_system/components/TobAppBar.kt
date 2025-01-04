package com.godzuche.achivitapp.core.design_system.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.design_system.theme.MOrange
import com.godzuche.achivitapp.core.presentation.util.ext.shimmerEffect
import com.godzuche.achivitapp.feature.auth.presentation.UserAuthState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    userAuthState: UserAuthState,
    scrollBehavior: TopAppBarScrollBehavior,
    onSettingsActionClicked: () -> Unit,
    onProfileIconClicked: () -> Unit,
    onTopBarTitleClicked: () -> Unit,
    modifier: Modifier = Modifier,
    todayTasks: Int = 0,
    isOnline: Boolean
) {
    val context = LocalContext.current

    when (userAuthState) {
        is UserAuthState.SignedIn -> {
            TopAppBar(
                modifier = modifier,
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Column(
                        modifier = Modifier.clickable(
                            enabled = true,
                            onClick = onTopBarTitleClicked
                        ),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = context.resources.getString(
                                R.string.greeting,
                                userAuthState.data.displayName ?: "No name"
                            ),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = pluralStringResource(
                                id = R.plurals.activeTasksMessage,
                                count = todayTasks,
                                formatArgs = arrayOf(todayTasks),
                            ),
                            fontSize = 14.sp,
                        )
                    }
                },
                navigationIcon = {
                    ProfilePhotoIcon(
                        profilePhotoUrl = userAuthState.data.profilePictureUrl,
                        onProfileIconClicked = onProfileIconClicked,
                        isOnline = isOnline,
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsActionClicked) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }

        UserAuthState.Loading -> {
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .shimmerEffect()
            )
        }

        else -> Unit
    }

}

@Composable
fun ProfilePhotoIcon(
    profilePhotoUrl: String?,
    onProfileIconClicked: () -> Unit,
    isOnline: Boolean,
) {
    val context = LocalContext.current
    val imageRequest = remember {
        ImageRequest.Builder(context)
            .data(profilePhotoUrl ?: R.drawable.avatar_12)
            .size(Size.ORIGINAL)
            .placeholder(R.drawable.avatar_12)
            .crossfade(true)
            .build()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .padding(6.dp)
    ) {
        IconButton(
            onClick = onProfileIconClicked,
            modifier = Modifier.size(48.dp)
        ) {
            AsyncImage(
                model = imageRequest,
                contentDescription = "Profile picture",
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        OnlineStatusIndicator(
            isOnline = isOnline,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
fun OnlineStatusIndicator(
    modifier: Modifier = Modifier,
    isOnline: Boolean,
) {
    val color by animateColorAsState(
        targetValue = if (isOnline) Color.Green else MOrange,
        label = "Online Indicator Color",
    )

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .size(12.dp)
            .drawWithCache {
                onDrawBehind {
                    drawCircle(color = backgroundColor)
                }
            }
            .padding(2.dp)
            .clip(CircleShape)
            .background(color)
            .then(modifier),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onExitSearch: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit),
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var expanded by remember {
        mutableStateOf(false)
    }
    var firstComposition by remember {
        mutableStateOf(true)
    }
    var expansionJob: Job? = null
    LaunchedEffect(expanded, firstComposition) {
        Log.d("SearchAppBar", expanded.toString())
        expansionJob?.cancel()
        if (!expanded && !firstComposition) {
            expansionJob = launch {
                // From the platform implementation, enter transition duration of search bar = 700L and exit = 450L
                delay(450L)
                onExitSearch()
            }
        }
        firstComposition = false
        expansionJob = null
    }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChanged,
                onSearch = onSearchTriggered,
                expanded = expanded,
                onExpandedChange = { isExpanded ->
                    expanded = isExpanded
                },
                modifier = modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                enabled = true,
                placeholder = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = AchivitIcons.Search,
                            contentDescription = stringResource(id = R.string.search),
                        )
                        Text(text = "Search tasks")
                    }
                },
                leadingIcon = {
                    IconButton(onClick = onExitSearch) {
                        Icon(
                            imageVector = AchivitIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back),
                        )
                    }
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
                            onClick = { onSearchQueryChanged("") }
                        ) {
                            Icon(
                                imageVector = AchivitIcons.Close,
                                contentDescription = stringResource(R.string.clear_search_text_content_desc)
                            )
                        }
                    }
                },
//                    colors = TODO(),
//                    interactionSource = TODO(),
            )
        },
        expanded = expanded,
        onExpandedChange = { isExpanded ->
            expanded = isExpanded
        },
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
//            shape = TODO(),
//            colors = TODO(),
//            tonalElevation = TODO(),
//            shadowElevation = TODO(),
        windowInsets = WindowInsets(0, 0, 0, 0),
        content = content,
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchivitTopBar(
    title: @Composable () -> Unit
) {
    TopAppBar(
        title = title
    )
}