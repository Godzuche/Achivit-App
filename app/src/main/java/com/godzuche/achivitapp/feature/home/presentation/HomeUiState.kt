package com.godzuche.achivitapp.feature.home.presentation

import com.godzuche.achivitapp.domain.model.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.domain.model.Task

data class HomeUiState(
    val todayTasks: List<Task> = emptyList(),
    val noneStatusCount: Int = 0,
    val todosTaskCount: Int = 0,
    val inProgressTaskCount: Int = 0,
    val lateTasksCount: Int = 0,
    val completedTasksCount: Int = 0,
//    val taskStatus: MutableMap<String, Int>? = null,
//    val categories: List<TaskCategory> = emptyList(),
//    val categoryWithCollectionsPairs: List<Pair<TaskCategory, List<TaskCollection>>>? = null
    val categoryWithCollectionsAndTasks: List<CategoryWithCollectionsAndTasks> = emptyList()
)