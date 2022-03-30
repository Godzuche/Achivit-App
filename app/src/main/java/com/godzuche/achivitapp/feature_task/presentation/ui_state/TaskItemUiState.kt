package com.godzuche.achivitapp.feature_task.presentation.ui_state

data class TaskItemUiState(
    val title: String,
    val description: String,
    val completed: Boolean = false,
)
