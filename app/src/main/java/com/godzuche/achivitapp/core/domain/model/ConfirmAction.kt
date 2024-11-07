package com.godzuche.achivitapp.core.domain.model

sealed interface ConfirmAction {
    data class DeleteTask(val task: Task) : ConfirmAction

    data object SignOut : ConfirmAction
}