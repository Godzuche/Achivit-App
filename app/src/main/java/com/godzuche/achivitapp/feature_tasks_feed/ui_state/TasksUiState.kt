package com.godzuche.achivitapp.feature_tasks_feed.ui_state

import com.godzuche.achivitapp.data.local.entity.TaskCategory
import com.godzuche.achivitapp.data.local.relations.CollectionWithTasks
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature_tasks_feed.util.TaskStatus

data class TasksUiState(
    val tasksItems: List<Task> = emptyList(),
    val checkedCategoryFilterChipId: Int = 0,
    val categoryFilter: String = "",
    val statusFilterId: Int = 0,
    val statusFilter: TaskStatus = TaskStatus.NONE,
    val checkedCollectionFilterChipId: Int = -1,
    val collectionFilter: String = "",
    val categories: List<TaskCategory> = emptyList(),
    val categoryAndCollectionsWithTasksPairs: List<Pair<TaskCategory, List<CollectionWithTasks>>>? = null
)
