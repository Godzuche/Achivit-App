package com.godzuche.achivitapp.presentation.tasks.util

import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity

data class TaskFilter(
    val category: TaskCategoryEntity? = null,
    val collection: TaskCollectionEntity? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
