package com.godzuche.achivitapp.feature.tasks.task_list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitAssistChip
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.design_system.theme.Alpha
import com.godzuche.achivitapp.core.design_system.theme.CompletedColor
import com.godzuche.achivitapp.core.design_system.theme.InProgressColor
import com.godzuche.achivitapp.core.design_system.theme.MBlue
import com.godzuche.achivitapp.core.design_system.theme.RunningLateColor
import com.godzuche.achivitapp.core.design_system.theme.TodoColor
import com.godzuche.achivitapp.core.design_system.theme.onSurfaceDark
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskStatus
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.core.ui.util.toModifiedStatusText
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class TasksTopBarActions {
    SEARCH,
    FILTER,
    SETTINGS,
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TasksRoute(
    tasksViewModel: TasksViewModel,
    onTopBarAction: (TasksTopBarActions) -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onAddNewTaskCategory: () -> Unit
) {
    val pagedTaskList = tasksViewModel.tasksPagingDataFlow
        .collectAsLazyPagingItems(tasksViewModel.viewModelScope.coroutineContext)
    val tasksUiState by tasksViewModel.uiState.collectAsStateWithLifecycle()

    TasksScreen(
        tasksUiState = tasksUiState,
        taskLazyPagingItems = pagedTaskList,
        onTopBarAction = onTopBarAction,
        onNavigateToTaskDetail = onNavigateToTaskDetail,
        onAddNewTaskCategory = onAddNewTaskCategory,
        onCategoryChipsCheckChanged = { index -> tasksViewModel.setCheckedCategoryChip(checkedId = index) },
        onTaskCheck = { task, isChecked -> tasksViewModel.setIsCompleted(task, isChecked) },
        onSwipeToDeleteTask = { task -> tasksViewModel.accept(TasksUiEvent.OnDeleteTask(task = task)) }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)
@Composable
fun TasksScreen(
    tasksUiState: TasksUiState,
    taskLazyPagingItems: LazyPagingItems<Task>,
    onTopBarAction: (TasksTopBarActions) -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onAddNewTaskCategory: () -> Unit,
    onCategoryChipsCheckChanged: (Int) -> Unit,
    onTaskCheck: (Task, Boolean) -> Unit,
    onSwipeToDeleteTask: (Task) -> Unit,
) {

    val topBarState = rememberTopAppBarState()
    val scrollBehaviour =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val taskListState = rememberLazyListState()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            // Todo: Create a separate state for categories in the viewModel so it can be passed to the top bar
            // Todo: Send the ui events to the ViewModel
            TasksTopBar(
                categories = tasksUiState.categories,
                checkedCategoryId = tasksUiState.checkedCategoryFilterChipId,
                scrollBehaviour = scrollBehaviour,
                onSelectCategory = { index, _ ->
                    onCategoryChipsCheckChanged(index)
                },
                onAddNewCategoryClick = onAddNewTaskCategory,
                onSearchActionClick = { onTopBarAction(TasksTopBarActions.SEARCH) },
                onSettingsActionClick = { onTopBarAction(TasksTopBarActions.SETTINGS) },
                onFilterActionClick = { onTopBarAction(TasksTopBarActions.FILTER) }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection)
    ) { padding ->

        // Todo: Add Shimmer Effect for refresh Loading state
        LazyColumn(
            state = taskListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets(bottom = 84.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            if (taskLazyPagingItems.itemCount == 0) {
                item { MessageItem(message = "Your tasks will appear here.") }
            } else {
                items(
                    count = taskLazyPagingItems.itemCount,
                    key = taskLazyPagingItems.itemKey { it.id!! },
                    contentType = taskLazyPagingItems.itemContentType { "TaskPagingItems" }
                ) { index ->
                    val task = taskLazyPagingItems[index]
                    task?.let {
                        SwipeToDismissTaskCard(
                            task = task,
                            onSwipeToDelete = { onSwipeToDeleteTask(task) },
                            onDoneCheck = { task, isChecked -> onTaskCheck(task, isChecked) },
                            onTaskClick = { onNavigateToTaskDetail(it.id!!) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }

            taskLazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingItem(modifier = Modifier.fillParentMaxSize()) }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { LoadingItem(modifier = Modifier.fillMaxWidth()) }
                    }

                    loadState.refresh is LoadState.Error -> {
                        val e = loadState.refresh as LoadState.Error
                        item { ErrorItem(message = e.error.localizedMessage!!) }
                    }

                    loadState.append is LoadState.Error -> {
                        val e = loadState.append as LoadState.Error
                        item { ErrorItem(message = e.error.localizedMessage!!) }
                    }
                }
            }

            if (taskLazyPagingItems.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorItem(message: String) {
    Text(text = "Error: $message")
}

@Composable
fun MessageItem(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissTaskCard(
    task: Task,
    onSwipeToDelete: () -> Unit,
    onDoneCheck: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue: SwipeToDismissBoxValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onSwipeToDelete()
                    false
                }

                else -> false
            }
        },
        positionalThreshold = { totalDistance ->
            // Swiping 20% of the total distance will trigger the target state
            // depending on the direction of swipe
            totalDistance * 0.2F
        }
    )
    val color by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = Alpha.MEDIUM_HIGH)
            }

            SwipeToDismissBoxValue.StartToEnd -> MBlue
            SwipeToDismissBoxValue.EndToStart -> Color.Red
        },
        label = "Dismiss Background Color",
    )
    val iconColor by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            SwipeToDismissBoxValue.Settled -> {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            SwipeToDismissBoxValue.StartToEnd -> onSurfaceDark
            SwipeToDismissBoxValue.EndToStart -> onSurfaceDark
        },
        label = "Dismiss Icon Color",
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            TaskDismissBackground(color = color, iconColor = iconColor)
        },
        content = {
            TaskCard(
                task = task,
                onTaskClick = onTaskClick,
                onDoneCheck = onDoneCheck,
            )
        }
    )
}

@Composable
fun TaskDismissBackground(
    color: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Todo: Maybe change snooze to swipe to mark completed.
            Icon(
                imageVector = Icons.Rounded.Snooze,
                tint = iconColor,
                contentDescription = stringResource(R.string.snooze_task),
            )
            Icon(
                imageVector = Icons.Rounded.Delete,
                tint = iconColor,
                contentDescription = stringResource(R.string.delete_task),
            )
        }
    }
}

@Composable
fun MCard(
    onClick: () -> Unit,
    overlineContent: @Composable (() -> Unit)? = null,
    headlineContent: @Composable () -> Unit,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                top = 12.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 24.dp
            )
        ) {
            leadingContent?.invoke()

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                overlineContent?.let {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.labelSmall.merge(
                            TextStyle(
//                                color = MaterialTheme.colorScheme.tertiary,
                                fontStyle = FontStyle.Italic,
                            )
                        ),
                        content = it
                    )
                }
                ProvideTextStyle(
                    value = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    content = headlineContent
                )
                if (supportingContent != null) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.bodyMedium.merge(
                            TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ),
                        content = supportingContent
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDoneCheck: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    val statusColor by animateColorAsState(
        targetValue = when (task.status) {
            TaskStatus.IN_PROGRESS -> InProgressColor.copy(alpha = Alpha.MEDIUM_HIGH)
            TaskStatus.COMPLETED -> CompletedColor.copy(alpha = Alpha.MEDIUM_HIGH)
            TaskStatus.TODO -> TodoColor.copy(alpha = Alpha.MEDIUM_HIGH)
            TaskStatus.RUNNING_LATE -> RunningLateColor.copy(alpha = Alpha.MEDIUM_HIGH)
            else -> TodoColor.copy(alpha = Alpha.MEDIUM_HIGH)
        },
        label = "Task Status Color"
    )

    MCard(onClick = { onTaskClick(task) },
        overlineContent = {
            Text(
                text = task.status.name.toModifiedStatusText(),
                color = statusColor,
            )
        },
        headlineContent = {
            Text(
                text = task.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            task.description.let {
                if (it.isNotEmpty()) {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            AchivitAssistChip(
                onClick = {},
                label = {
                    Text(text = task.dueDate.millisToString())
                },
                leadingIcon = {
                    Icon(
                        imageVector = AchivitIcons.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        },
        leadingContent = {
            DoneCheckBox(
                checked = task.isCompleted,
                onCheckChanged = { isChecked -> onDoneCheck(task, isChecked) }
            )
        },
        trailingContent = {
            TaskStatusColor(color = statusColor)
        }
    )
}

@Preview
@Composable
fun SwipeToDismissCardPreview() {
    SwipeToDismissTaskCard(
        task = Task(
            title = "Read a book",
            description = "An Android Dev related book \\n Also cook a meal for dinner.",
            created = 0L,
            dueDate = 0L,
            categoryTitle = "My Tasks",
            collectionTitle = "All Tasks"
        ),
        onSwipeToDelete = {},
        onDoneCheck = { _, _ -> },
        onTaskClick = { }
    )
}
