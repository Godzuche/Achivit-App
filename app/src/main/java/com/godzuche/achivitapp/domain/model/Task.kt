package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val created: Long,
    val dueDate: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val collectionTitle: String,
    val categoryTitle: String
)

fun Task.asNewEntity() = TaskEntity(
    title = title,
    description = description,
    isCompleted = isCompleted,
    created = created,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)

fun Task.asEntity() = TaskEntity(
    id = id!!,
    title = title,
    isCompleted = isCompleted,
    created = created,
    description = description,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)