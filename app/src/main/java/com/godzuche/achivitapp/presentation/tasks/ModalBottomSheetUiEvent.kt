package com.godzuche.achivitapp.presentation.tasks

sealed class ModalBottomSheetUiEvent {
    data class OnGetBottomSheetAction(val taskId: Int) : ModalBottomSheetUiEvent()
}
