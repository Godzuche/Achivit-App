package com.godzuche.achivitapp.feature_task.presentation.util

import com.godzuche.achivitapp.feature_task.domain.model.Task

sealed class TaskUiEvent {
    data class OnRetrieveTask(val taskId: Long) : TaskUiEvent()
    data class OnDeleteTask(val task: Task) : TaskUiEvent()
    object OnUndoDeleteClick : TaskUiEvent()
    data class OnUpdateTask(val task: Task, val isDone: Boolean) : TaskUiEvent()
    object OnUpdateTaskClick : TaskUiEvent()
    object OnNavigateUp : TaskUiEvent()
}
