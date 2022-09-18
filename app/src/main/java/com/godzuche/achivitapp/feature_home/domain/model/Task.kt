package com.godzuche.achivitapp.feature_home.domain.model

import com.godzuche.achivitapp.feature_home.data.local.entity.TaskEntity
import com.godzuche.achivitapp.feature_home.presentation.util.TaskStatus

data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    // val isPriority: Boolean = false
    val created: Long? = null,
    val dueDate: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val collectionTitle: String,
) {
    fun toNewTaskEntity(): TaskEntity {
        return TaskEntity(
            title = title,
            description = description,
            isCompleted = isCompleted,
            created = created!!,
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
            description = description,
            dueDate = dueDate,
            status = status,
            collectionTitle = collectionTitle
        )
    }
}
