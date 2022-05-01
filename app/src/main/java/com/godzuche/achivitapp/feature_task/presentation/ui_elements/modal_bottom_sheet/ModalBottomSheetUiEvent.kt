package com.godzuche.achivitapp.feature_task.presentation.ui_elements.modal_bottom_sheet

sealed class ModalBottomSheetUiEvent {
    data class OnGetBottomSheetAction(val taskId: Int) : ModalBottomSheetUiEvent()
}
