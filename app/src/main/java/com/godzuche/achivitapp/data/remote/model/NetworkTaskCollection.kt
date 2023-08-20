package com.godzuche.achivitapp.data.remote.model

import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity

data class NetworkTaskCollection(
    val title: String,
    val categoryTitle: String,
) {
    @Suppress("unused")
    constructor() : this("", "")
}

fun NetworkTaskCollection.asEntity() =
    TaskCollectionEntity(
        title = title,
        categoryTitle = categoryTitle,
    )