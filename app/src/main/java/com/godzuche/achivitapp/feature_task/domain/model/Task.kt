package com.godzuche.achivitapp.feature_task.domain.model

import com.godzuche.achivitapp.feature_task.data.local.entity.TaskEntity
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
//    val completed: Boolean = false,
    val date: Long,
    val hours: Int,
    val minutes: Int,
    val status: TaskStatus = TaskStatus.TODO,
) {
    fun toNewTaskEntity(): TaskEntity {
        return TaskEntity(
            title = title,
            description = description,
//            completed = completed,
            date = date,
            hours = hours,
            minutes = minutes,
            status = status
        )
    }

    fun toTaskEntity(): TaskEntity {
        return TaskEntity(
            id = id!!,
            title = title,
            description = description,
//            completed = completed,
            date = date,
            hours = hours,
            minutes = minutes,
            status = status
        )
    }
}
