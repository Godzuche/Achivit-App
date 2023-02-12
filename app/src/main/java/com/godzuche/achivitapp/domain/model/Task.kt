package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.entity.TaskEntity
import com.godzuche.achivitapp.feature_tasks_feed.util.TaskStatus

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    // val isPriority: Boolean = false
    val created: Long,
    val dueDate: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val collectionTitle: String,
    val categoryTitle: String
) {
    fun toNewTaskEntity(): TaskEntity {
        return TaskEntity(
            title = title,
            description = description,
            isCompleted = isCompleted,
            created = created,
            dueDate = dueDate,
            status = status,
            collectionTitle = collectionTitle,
            categoryTitle = categoryTitle
        )
    }

    fun toTaskEntity(): TaskEntity {
        return TaskEntity(
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
    }
}
