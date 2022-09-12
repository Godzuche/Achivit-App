package com.godzuche.achivitapp.feature_home.presentation.ui_elements.task_details

import com.godzuche.achivitapp.feature_home.domain.model.Task

sealed class TaskUiEvent {
    data class OnRetrieveTask(val taskId: Int) : TaskUiEvent()
    data class OnDeleteTask(val task: Task) : TaskUiEvent()
    object OnUndoDeleteClick : TaskUiEvent()
    data class OnUpdateTask(val task: Task, val isDone: Boolean) : TaskUiEvent()
    object OnUpdateTaskClick : TaskUiEvent()
    object OnNavigateUp : TaskUiEvent()
}
