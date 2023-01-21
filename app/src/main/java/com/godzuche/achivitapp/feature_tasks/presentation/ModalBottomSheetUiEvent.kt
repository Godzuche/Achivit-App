package com.godzuche.achivitapp.feature_tasks.presentation

sealed class ModalBottomSheetUiEvent {
    data class OnGetBottomSheetAction(val taskId: Int) : ModalBottomSheetUiEvent()
}
