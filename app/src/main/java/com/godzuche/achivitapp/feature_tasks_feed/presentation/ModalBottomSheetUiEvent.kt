package com.godzuche.achivitapp.feature_tasks_feed.presentation

sealed class ModalBottomSheetUiEvent {
    data class OnGetBottomSheetAction(val taskId: Int) : ModalBottomSheetUiEvent()
}
