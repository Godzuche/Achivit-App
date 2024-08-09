package com.godzuche.achivitapp.feature.tasks.ui_state

import com.godzuche.achivitapp.core.domain.model.Task

data class ModalBottomSheetUiState(
    val bottomSheetActionTitle: String = "",
    val task: Task? = null,
    val taskId: Int = -1,
    //

    val categorySelection: String = "",
    val collectionSelection: String = "",
    val categories: List<String> = emptyList(),
    val categoryCollections: List<String> = emptyList(),
    val taskTitle: String = "",
    val descriptionText: String = "",
    val dateTimeSelection: Long = 0L,
)

