package com.godzuche.achivitapp.core.presentation.state

import com.godzuche.achivitapp.core.domain.model.AchivitDialog

data class DialogUiState(
    val shouldShow: Boolean = false,
    val dialog: AchivitDialog? = null
)