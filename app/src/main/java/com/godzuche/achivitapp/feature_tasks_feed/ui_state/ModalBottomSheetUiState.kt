package com.godzuche.achivitapp.feature_tasks_feed.ui_state

import com.godzuche.achivitapp.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetActionTitle: String = "",
    val task: Task? = null,
    val taskId: Int = -1,
    //

    /*val categorySelection: String,
    val collectionSelection: String,
    val taskTitle: String,
    val descriptionText: String,*/
    val dateTimeSelection: Long = 0L,
)

