package com.godzuche.achivitapp.feature.tasks.task_detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitAssistChip
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.design_system.theme.AchivitDimension
import com.godzuche.achivitapp.core.design_system.theme.AchivitTheme
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.core.domain.model.Task

@Composable
fun TaskDetailRoute(
    onNavigateBack: () -> Unit,
    onDeleteTask: (Task) -> Unit,
    taskDetailViewModel: TaskDetailViewModel = hiltViewModel()
) {
    val taskDetailUiState by taskDetailViewModel.detail.collectAsStateWithLifecycle()

    TaskDetailScreen(
        detailUiState = taskDetailUiState,
        navigateBack = onNavigateBack,
        onDeleteTask = onDeleteTask
    )
}

@OptIn(
    ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun TaskDetailScreen(
    detailUiState: TaskDetailUiState,
    navigateBack: () -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = AchivitIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.go_back)
                        )
                    }
                },
                actions = {
                    when (detailUiState) {
                        is TaskDetailUiState.Success -> {
                            IconButton(
                                onClick = {
                                    detailUiState.data.let(onDeleteTask)
                                }
                            ) {
                                Icon(
                                    imageVector = AchivitIcons.Delete,
                                    contentDescription = "Delete task"
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            )
        }
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
        ) {
            when (detailUiState) {
                // Todo: create a taskUiState with Loading and Success instead of nullable detail
                is TaskDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator()
                    }
                }

                is TaskDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = detailUiState.exception?.localizedMessage ?: "An error occured!"
                        )
                    }
                }

                is TaskDetailUiState.Success -> {
                    LazyVerticalGrid(
                        state = lazyGridState,
                        columns = GridCells.Adaptive(AchivitDimension.minVerticalGridColumnWidth),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Text(
                                text = detailUiState.data.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 16.dp),
                                fontSize = 28.sp
                            )
                        }
                        if (detailUiState.data.description.isNotBlank()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Row(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .animateItemPlacement(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = AchivitIcons.Description,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = detailUiState.data.description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = AchivitIcons.DateRange,
                                    contentDescription = null
                                )
                                AchivitAssistChip(
                                    onClick = {},
                                    label = {
                                        Text(text = detailUiState.data.dueDate.millisToString())
                                    }
                                )
                            }
                        }
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.created,
                                        detailUiState.data.created.millisToString()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TaskDetailScreenPreview() {
    AchivitTheme {
        TaskDetailScreen(
            detailUiState = /*Task(
                id = null,
                title = "Read a book",
                description = "An Android Dev related book",
                created = 0L,
                dueDate = 0L,
                collectionTitle = "All Tasks",
                categoryTitle = "My Tasks"
            )*/ TaskDetailUiState.Success(
                data = Task(
                    id = null,
                    title = "Read a book",
                    description = "An Android Dev related book",
                    created = 0L,
                    dueDate = 0L,
                    collectionTitle = "All Tasks",
                    categoryTitle = "My Tasks"
                )
            ),
            navigateBack = {},
            onDeleteTask = {}
        )
    }
}