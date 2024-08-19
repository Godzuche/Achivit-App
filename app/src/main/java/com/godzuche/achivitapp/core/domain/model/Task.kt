package com.godzuche.achivitapp.core.domain.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity

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
    completed = isCompleted,
    created = created,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)

fun Task.asEntity() = TaskEntity(
    id = id!!,
    title = title,
    completed = isCompleted,
    created = created,
    description = description,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)