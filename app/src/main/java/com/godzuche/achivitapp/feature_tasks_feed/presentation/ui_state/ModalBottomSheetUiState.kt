package com.godzuche.achivitapp.feature_tasks_feed.presentation.ui_state

import com.godzuche.achivitapp.core.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetAction: String = "",
    val task: Task? = null,
    val id: Int = -1,
    //

    val categorySelection: String,
    val collectionSelection: String,
    val taskTitle: String,
    val descriptionText: String,
    val dateTimeSelection: Long = 0L,
)

