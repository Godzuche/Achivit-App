package com.godzuche.achivitapp.feature_task.presentation.ui_state

import com.godzuche.achivitapp.feature_task.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetAction: String = "",
    val task: Task? = null,
    val id: Int = -1,
)

