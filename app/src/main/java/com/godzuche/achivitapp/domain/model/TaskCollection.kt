package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity

data class TaskCollection(
    val title: String,
    val categoryTitle: String,
)

fun TaskCollection.asEntity() =
    TaskCollectionEntity(
        title = title,
        categoryTitle = categoryTitle,
    )