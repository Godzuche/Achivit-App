package com.godzuche.achivitapp.feature_task.presentation.home

import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategory
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection

data class HomeUiState(
    val noneStatusCount: Int = 0,
    val todosTaskCount: Int = 0,
    val inProgressTaskCount: Int = 0,
    val lateTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
    val categoryWithCollectionsPairs: List<Pair<TaskCategory, List<TaskCollection>>>? = null
)