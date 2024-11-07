package com.godzuche.achivitapp.core.domain.model

sealed interface AchivitDialog {
    data class ConfirmationDialog(val action: ConfirmAction) : AchivitDialog
}