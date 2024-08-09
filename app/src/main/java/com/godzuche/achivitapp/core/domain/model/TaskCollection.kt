package com.godzuche.achivitapp.core.domain.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity

data class TaskCollection(
    val title: String,
    val categoryTitle: String,
)

fun TaskCollection.asEntity() =
    TaskCollectionEntity(
        title = title,
        categoryTitle = categoryTitle,
    )