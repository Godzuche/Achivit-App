package com.godzuche.achivitapp.feature_home.presentation.util

import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCollectionEntity

data class TaskFilter(
    val category: TaskCategoryEntity? = null,
    val collection: TaskCollectionEntity? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
