package com.godzuche.achivitapp.feature_task.presentation.ui_state

import com.godzuche.achivitapp.feature_task.domain.model.Task

data class TasksUiState(
    val tasksItems: List<Task> = emptyList(),
//    val filters: List<TasksFilter> = emptyList(),
    /* val loading: Boolean = false,
     val userMessage: List<Message> = emptyList(),*/
//    val lastScrolledPosition: Int = 0,
//    val hasNotScrolledForCurrentSearch: Boolean = false,
)
