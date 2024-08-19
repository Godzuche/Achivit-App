package com.godzuche.achivitapp.feature.home.presentation

import com.godzuche.achivitapp.core.domain.model.CategoryWithCollectionsAndTasks
import com.godzuche.achivitapp.core.domain.model.Task

data class HomeUiState(
    val todayTasks: List<Task> = emptyList(),
    val noneStatusOverview: TaskStatusOverview = TaskStatusOverview.None(),
    val todoStatusOverview: TaskStatusOverview = TaskStatusOverview.Todo(),
    val inProgressStatusOverview: TaskStatusOverview = TaskStatusOverview.InProgress(),
    val runningLateStatusOverview: TaskStatusOverview = TaskStatusOverview.RunningLate(),
    val completedStatusOverview: TaskStatusOverview = TaskStatusOverview.Completed(),
    val categoryWithCollectionsAndTasks: List<CategoryWithCollectionsAndTasks> = emptyList()
) {
    val taskStatusOverviews = listOf(
        noneStatusOverview,
        todoStatusOverview,
        inProgressStatusOverview,
        runningLateStatusOverview,
        completedStatusOverview,
    )
}