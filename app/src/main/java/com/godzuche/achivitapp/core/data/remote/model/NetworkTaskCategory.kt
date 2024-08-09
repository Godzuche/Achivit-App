package com.godzuche.achivitapp.core.data.remote.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity

data class NetworkTaskCategory(
    val title: String,
    val created: Long,
) {
    @Suppress("unused")
    constructor() : this("", 0L)
}

fun NetworkTaskCategory.asEntity() = TaskCategoryEntity(
    title = title,
    created = created
)