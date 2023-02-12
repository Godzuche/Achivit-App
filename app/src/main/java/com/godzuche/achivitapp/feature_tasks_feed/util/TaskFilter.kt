package com.godzuche.achivitapp.feature_tasks_feed.util

import com.godzuche.achivitapp.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.entity.TaskCollectionEntity

data class TaskFilter(
    val category: TaskCategoryEntity? = null,
    val collection: TaskCollectionEntity? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
