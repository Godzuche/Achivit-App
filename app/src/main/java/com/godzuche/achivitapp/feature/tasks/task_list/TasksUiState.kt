package com.godzuche.achivitapp.feature.tasks.task_list

import androidx.compose.runtime.Stable
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskCategory
import com.godzuche.achivitapp.core.domain.model.TaskStatus

@Stable
data class TasksUiState(
    val tasksItems: List<Task> = emptyList(),
    val checkedCategoryFilterChipId: Int = 0,
    val statusFilterId: Int = 0,
    val statusFilter: TaskStatus = TaskStatus.NONE,
    val checkedCollectionFilterChipId: Int = -1,
    val collectionFilter: String = "",
    val categories: List<TaskCategory> = emptyList(),
    val categoryFilter: String = "",
)
