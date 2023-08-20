package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity

data class TaskCategory(
    val title: String,
    val created: Long,
)

fun TaskCategory.asEntity() = TaskCategoryEntity(
    title = title,
    created = created
)