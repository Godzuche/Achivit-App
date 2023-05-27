package com.godzuche.achivitapp.feature.feed.util

import com.godzuche.achivitapp.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.entity.TaskCollectionEntity

data class TaskFilter(
    val category: TaskCategoryEntity? = null,
    val collection: TaskCollectionEntity? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
