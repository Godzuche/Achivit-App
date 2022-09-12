package com.godzuche.achivitapp.feature_home.presentation.ui_state

import com.godzuche.achivitapp.feature_home.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetAction: String = "",
    val task: Task? = null,
    val id: Int = -1,
)

