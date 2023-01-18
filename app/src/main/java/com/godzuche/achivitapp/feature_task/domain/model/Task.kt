package com.godzuche.achivitapp.feature_task.domain.model

import com.godzuche.achivitapp.feature_task.data.local.entity.TaskEntity
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus

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
) {
    fun toNewTaskEntity(): TaskEntity {
        return TaskEntity(
            title = title,
            description = description,
            isCompleted = isCompleted,
            created = created,
            dueDate = dueDate,
            status = status,
            collectionTitle = collectionTitle
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
            collectionTitle = collectionTitle
        )
    }
}
