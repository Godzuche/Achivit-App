package com.godzuche.achivitapp.feature_task.presentation

import com.godzuche.achivitapp.feature_task.domain.model.Task

sealed class TasksUiEvent {
    data class Search(val query: String) : TasksUiEvent()
    data class OnSearch(val query: String) : TasksUiEvent()
    data class OnDeleteTask(val task: Task, val shouldPopBackStack: Boolean = false) :
        TasksUiEvent()

    data class OnDoneChange(val task: Task, val isDone: Boolean) : TasksUiEvent()
    object OnUndoDeleteClick : TasksUiEvent()
    object OnAddTaskClick : TasksUiEvent()
    data class OnTaskClick(val task: Task) : TasksUiEvent()
}