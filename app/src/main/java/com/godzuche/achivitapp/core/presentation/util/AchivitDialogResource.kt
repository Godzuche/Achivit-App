package com.godzuche.achivitapp.core.presentation.util

import com.godzuche.achivitapp.core.domain.model.ConfirmAction

interface DialogResourceProvider {
    val title: String?
    val description: String?
    val dismissLabel: String?
    val confirmLabel: String?
}

class ConfirmationResourceProvider(action: ConfirmAction) : DialogResourceProvider {
    override val title: String? = when (action) {
        is ConfirmAction.DeleteTask -> "Delete Task"
        is ConfirmAction.SignOut -> "Sign Out"
    }
    override val description: String? = when (action) {
        is ConfirmAction.DeleteTask -> "Are you sure you want to delete this task?"
        is ConfirmAction.SignOut -> "Are you sure you want to sign out?"
    }
    override val dismissLabel: String? = when (action) {
        is ConfirmAction.DeleteTask -> "No, cancel"
        is ConfirmAction.SignOut -> "No, cancel"
    }
    override val confirmLabel: String? = when (action) {
        is ConfirmAction.DeleteTask -> "Yes, delete"
        is ConfirmAction.SignOut -> "Yes, sign out"
    }
}