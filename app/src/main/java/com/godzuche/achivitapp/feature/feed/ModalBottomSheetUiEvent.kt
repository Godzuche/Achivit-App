package com.godzuche.achivitapp.feature.feed

sealed class ModalBottomSheetUiEvent {
    data class OnGetBottomSheetAction(val taskId: Int) : ModalBottomSheetUiEvent()
}
