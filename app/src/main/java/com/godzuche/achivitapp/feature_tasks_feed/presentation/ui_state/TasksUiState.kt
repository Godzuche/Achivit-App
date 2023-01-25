package com.godzuche.achivitapp.feature_tasks_feed.presentation.ui_state

import com.godzuche.achivitapp.core.data.local.entity.TaskCategory
import com.godzuche.achivitapp.core.data.local.relations.CollectionWithTasks
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.feature_tasks_feed.presentation.util.TaskStatus

data class TasksUiState(
    val tasksItems: List<Task> = emptyList(),
    val checkCategoryFilterChipId: Int = 0,
    val categoryFilter: String = "",
    val statusFilterId: Int = 0,
    val statusFilter: TaskStatus = TaskStatus.NONE,
//    val filters: List<TasksFilter> = emptyList(),
    /* val loading: Boolean = false,
     val userMessage: List<Message> = emptyList(),*/
//    val lastScrolledPosition: Int = 0,
//    val hasNotScrolledForCurrentSearch: Boolean = false,
    val categories: List<TaskCategory> = emptyList(),
    val categoryAndCollectionsWithTasksPairs: List<Pair<TaskCategory, List<CollectionWithTasks>>>? = null
)
