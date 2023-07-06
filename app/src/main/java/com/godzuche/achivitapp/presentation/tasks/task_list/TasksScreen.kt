package com.godzuche.achivitapp.presentation.tasks.task_list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.godzuche.achivitapp.core.design_system.components.AchivitAssistChip
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.design_system.theme.MGreen
import com.godzuche.achivitapp.core.design_system.theme.MOrange
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.presentation.home.presentation.toModifiedStatusText
import com.godzuche.achivitapp.presentation.tasks.ui_state.TasksUiState
import com.godzuche.achivitapp.presentation.tasks.util.TaskStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class TasksTopBarActions {
    SEARCH,
    FILTER,
    SETTINGS
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TasksRoute(
    tasksViewModel: TasksViewModel,
    onTopBarAction: (TasksTopBarActions) -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onAddNewTaskCategory: () -> Unit
) {
    val pagedTaskList = tasksViewModel.tasksPagingDataFlow.collectAsLazyPagingItems()
    val tasksUiState by tasksViewModel.uiState.collectAsStateWithLifecycle()

    TasksScreen(
        tasksUiState = tasksUiState,
        tasks = pagedTaskList,
        onTopBarAction = onTopBarAction,
        onNavigateToTaskDetail = onNavigateToTaskDetail,
        onAddNewTaskCategory = onAddNewTaskCategory,
        onCategoryChipsCheckChanged = { index -> tasksViewModel.setCheckedCategoryChip(checkedId = index) },
        onTaskCheck = { task, isChecked -> tasksViewModel.setIsCompleted(task, isChecked) },
        onSwipeToDeleteTask = { task -> tasksViewModel.accept(TasksUiEvent.OnDeleteTask(task = task)) }
    )
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TasksScreen(
    tasksUiState: TasksUiState,
    tasks: LazyPagingItems<Task>,
    onTopBarAction: (TasksTopBarActions) -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onAddNewTaskCategory: () -> Unit,
    onCategoryChipsCheckChanged: (Int) -> Unit,
    onTaskCheck: (Task, Boolean) -> Unit,
    onSwipeToDeleteTask: (Task) -> Unit
) {

    val topBarState = rememberTopAppBarState()
    val scrollBehaviour =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)
    val taskListState = rememberLazyListState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            // Todo: Create a separate state for categories in the viewModel so it can be passed to the top bar
            // Todo: Send the ui events to the ViewModel
            TasksTopBar(
                categories = tasksUiState.categories,
                checkedCategoryId = tasksUiState.checkedCategoryFilterChipId,
                scrollBehaviour = scrollBehaviour,
                onSelectCategory = { index, title ->
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
        if (tasks.itemCount == 0) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No task created yet.")
            }
        }
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
            // Todo: Use a when statement for the different load states
            items(
                count = tasks.itemCount,
                key = tasks.itemKey(key = { it.id!! }),
                contentType = tasks.itemContentType()
            ) { index ->
                val task = tasks[index]
                task?.let {
                    SwipeToDismissTaskCard(
                        task = task,
                        onSwipeToDelete = {
                            onSwipeToDeleteTask(task)
                        },
                        onDoneCheck = { task, isChecked -> onTaskCheck(task, isChecked) },
                        onTaskClick = {
                            onNavigateToTaskDetail(it.id!!)
                        },
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
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
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue: DismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
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
            DismissValue.Default -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6F)
            DismissValue.DismissedToEnd -> MGreen
            DismissValue.DismissedToStart -> Color.Red
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            Card(
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = Icons.Rounded.Snooze, contentDescription = null)
                    Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                }
            }
        },
        dismissContent = {
            TaskCard(
                task = task,
                onTaskClick = onTaskClick,
                onDoneCheck = onDoneCheck
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
//            DoneCheckBox(checked = false, onCheckChanged = {})
            if (leadingContent != null) {
                leadingContent()
            }
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                if (overlineContent != null) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.labelSmall.merge(
                            TextStyle(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ),
                        content = overlineContent
                    )
                }
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodyLarge.merge(
                        TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ),
                    content = headlineContent
                )
                if (supportingContent != null) {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.bodyMedium.merge(
                            TextStyle(
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        ),
                        content = supportingContent
                    )
                }
                /*Text(
                    text = "In Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Go home",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "To go cook and then go to the market to get somethings before calling it a day. I'd sleep afterwards.",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AchivitAssistChip(
                    onClick = { },
                    label = { Text(text = "Wed, Jun 29, 11:01 am") },
                    leadingIcon = {
                        Icon(
                            imageVector = AchivitIcons.AccessTime,
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.onSurface
                    )
                )*/
            }
//            TaskStatusColor(color = MOrange)
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
            TaskStatus.IN_PROGRESS -> MOrange
            TaskStatus.COMPLETED -> MGreen
            else -> Color.Gray
        }
    )

    var isTaskChecked = task.isCompleted

    MCard(onClick = { onTaskClick(task) },
        headlineContent = {
            Text(
                text = task.title
//                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
        overlineContent = {
            Text(text = task.status.name.toModifiedStatusText())
        },
        leadingContent = {
            DoneCheckBox(
                checked = isTaskChecked,
                onCheckChanged = { isChecked ->
                    onDoneCheck(task, isChecked)
                    isTaskChecked = isChecked
                }
            )
        },
        trailingContent = {
            TaskStatusColor(color = statusColor)
        }
    )

    /*    Card(
            onClick = { onTaskClick(task) },
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            text = task.description,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis
                        )
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
                    }
                },
                overlineContent = {
                    Text(text = task.status.name)
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.padding(8.dp),
                leadingContent = {
                    DoneCheckBox(
                        checked = isTaskChecked,
                        onCheckChanged = { isChecked ->
                            onDoneCheck(task, isChecked)
                            isTaskChecked = isChecked
                        }
                    )
                },
                trailingContent = {
                    TaskStatusColor(color = statusColor)
                }
            )
        }*/
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