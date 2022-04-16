package com.godzuche.achivitapp.feature_task.presentation.ui_elements.modal_bottom_sheet

import com.godzuche.achivitapp.feature_task.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetAction: String = "",
    val task: Task? = null,
    val id: Long = -1L,
)

