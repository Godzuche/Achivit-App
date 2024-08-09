package com.godzuche.achivitapp.feature.notifications

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.domain.model.Notification
import kotlinx.datetime.Instant

@Composable
fun NotificationRoute(
    modifier: Modifier = Modifier,
    onNotificationClick: (com.godzuche.achivitapp.core.domain.model.Notification) -> Unit,
    notificationsViewModel: NotificationsViewModel = hiltViewModel()
) {
    val notificationUiState by notificationsViewModel.notificationUiState.collectAsStateWithLifecycle()

    NotificationScreen(
        notificationUiState = notificationUiState,
        onNotificationClick = { notification ->
            notificationsViewModel.readNotification(notification)
            onNotificationClick(notification)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notificationUiState: NotificationUiState,
    onNotificationClick: (com.godzuche.achivitapp.core.domain.model.Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    val isNotificationLoading = notificationUiState is NotificationUiState.Loading

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.notifications))
                }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
        ) {
            when (notificationUiState) {
                NotificationUiState.Loading -> Unit
                is NotificationUiState.Success -> {
                    if (notificationUiState.notifications.isNotEmpty()) {
                        NotificationsGrid(
                            notificationUiState = notificationUiState,
                            onNotificationClick = onNotificationClick,
                            modifier = modifier
                        )
                    } else {
                        EmptyNotificationState(modifier)
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = isNotificationLoading,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun NotificationsGrid(
    notificationUiState: NotificationUiState,
    onNotificationClick: (com.godzuche.achivitapp.core.domain.model.Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()

    when (notificationUiState) {
        NotificationUiState.Loading -> Unit

        is NotificationUiState.Success -> LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
//            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalArrangement = Arrangement.spacedBy(24.dp),
            state = lazyGridState,
            modifier = modifier.fillMaxSize()
        ) {
            items(items = notificationUiState.notifications,
                span = { GridItemSpan(maxLineSpan) }
            ) { notification ->
                notification.run {
                    Notification(
                        modifier = Modifier,
                        title = title,
                        content = content,
                        isRead = isRead,
                        date = date,
                        onNotificationClick = { onNotificationClick(notification) }
                    )
                }
            }
        }
    }
}

@Composable
fun Notification(
    title: String,
    content: String,
    date: Instant,
    isRead: Boolean,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color =
        if (isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
    Surface(
        color = color,
        onClick = onNotificationClick
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "The task \"$title\" is due",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyNotificationState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = AchivitIcons.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "No notifications",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Received notifications will appear here",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}