package com.godzuche.achivitapp.feature_tasks.presentation.util

import com.godzuche.achivitapp.core.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.entity.TaskCollectionEntity

data class TaskFilter(
    val category: TaskCategoryEntity? = null,
    val collection: TaskCollectionEntity? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
