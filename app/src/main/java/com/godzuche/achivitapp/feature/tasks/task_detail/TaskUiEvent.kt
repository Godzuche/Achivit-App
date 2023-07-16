package com.godzuche.achivitapp.feature.tasks.task_detail

import com.godzuche.achivitapp.domain.model.Task

sealed class TaskUiEvent {
    data class OnRetrieveTask(val taskId: Int) : TaskUiEvent()
    data class OnDeleteTask(val task: Task) : TaskUiEvent()

    //    object OnUndoDeleteClick : TaskUiEvent()
    data class OnUpdateTask(val task: Task, val isDone: Boolean) : TaskUiEvent()
    /*    data class OnDeleteConfirm(
            val task: Task,
            val shouldScrollToTop: Boolean = false,
            val shouldScrollToBottom: Boolean = false,*//* val shouldPopBackStack: Boolean = false*//*
    ) : TaskUiEvent()*/

    object OnUpdateTaskClick : TaskUiEvent()
    object OnNavigateUp : TaskUiEvent()
}
