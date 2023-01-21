package com.godzuche.achivitapp.feature_tasks.presentation.ui_state

import com.godzuche.achivitapp.core.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetAction: String = "",
    val task: Task? = null,
    val id: Int = -1,
)

