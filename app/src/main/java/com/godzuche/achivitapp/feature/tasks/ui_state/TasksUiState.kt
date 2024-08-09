package com.godzuche.achivitapp.feature.tasks.ui_state

import com.godzuche.achivitapp.core.data.local.database.relations.CollectionWithTasksEntities
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskCategory
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus

data class TasksUiState(
    val tasksItems: List<Task> = emptyList(),
    val checkedCategoryFilterChipId: Int = 0,
    val statusFilterId: Int = 0,
    val statusFilter: TaskStatus = TaskStatus.NONE,
    val checkedCollectionFilterChipId: Int = -1,
    val collectionFilter: String = "",
    val categories: List<TaskCategory> = emptyList(),
    val categoryFilter: String = "",
    val categoryAndCollectionsWithTasksPairs: List<Pair<TaskCategory, List<CollectionWithTasksEntities>>>? = null
)
