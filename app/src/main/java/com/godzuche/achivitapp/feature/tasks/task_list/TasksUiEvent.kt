package com.godzuche.achivitapp.feature.tasks.task_list

import com.godzuche.achivitapp.core.domain.model.Task

sealed interface TasksUiEvent {
    data class Search(val query: String) : TasksUiEvent
    data class OnSearch(val query: String) : TasksUiEvent
    data class OnScroll(val currentScrollPosition: Int) : TasksUiEvent
    data class OnDeleteTask(val task: Task) : TasksUiEvent
    data class OnDeleteConfirm(
        val task: Task,
        val shouldScrollToTop: Boolean = false,
        val shouldScrollToBottom: Boolean = false,/* val shouldPopBackStack: Boolean = false*/
    ) : TasksUiEvent

    data class OnDeleteFromTaskDetail(val deletedTask: Task) : TasksUiEvent
    data class OnDoneChange(val task: Task, val isDone: Boolean) : TasksUiEvent
    object OnUndoDeleteClick : TasksUiEvent
    object OnAddTaskClick : TasksUiEvent
    data class OnTaskClick(val task: Task) : TasksUiEvent
}