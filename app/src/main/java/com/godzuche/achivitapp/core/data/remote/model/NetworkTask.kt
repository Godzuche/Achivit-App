package com.godzuche.achivitapp.core.data.remote.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus

data class NetworkTask(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val created: Long,
    val dueDate: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val collectionTitle: String,
    val categoryTitle: String,
) {
    @Suppress("unused")
    constructor() : this(0, "", "", false, 0L, 0L, TaskStatus.TODO, "", "")
}

fun NetworkTask.asEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    completed = completed,
    created = created,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)