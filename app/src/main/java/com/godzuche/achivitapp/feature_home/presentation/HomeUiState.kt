package com.godzuche.achivitapp.feature_home.presentation

import com.godzuche.achivitapp.core.data.local.entity.TaskCategory
import com.godzuche.achivitapp.core.data.local.relations.CollectionWithTasks
import com.godzuche.achivitapp.core.domain.model.Task

data class HomeUiState(
    val todayTasks: List<Task> = emptyList(),
    val noneStatusCount: Int = 0,
    val todosTaskCount: Int = 0,
    val inProgressTaskCount: Int = 0,
    val lateTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
//    val taskStatus: MutableMap<String, Int>? = null,
    val categories: List<TaskCategory> = emptyList(),
//    val categoryWithCollectionsPairs: List<Pair<TaskCategory, List<TaskCollection>>>? = null
    val categoryAndCollectionsWithTasksPairs: List<Pair<TaskCategory, List<CollectionWithTasks>>>? = null
)