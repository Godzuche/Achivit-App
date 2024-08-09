package com.godzuche.achivitapp.core.domain.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity

data class TaskCategory(
    val title: String,
    val created: Long,
)

fun TaskCategory.asEntity() = TaskCategoryEntity(
    title = title,
    created = created
)