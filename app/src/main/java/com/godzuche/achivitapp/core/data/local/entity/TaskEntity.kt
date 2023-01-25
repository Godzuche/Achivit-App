package com.godzuche.achivitapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.feature_tasks_feed.presentation.util.TaskStatus

@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "created")
    val created: Long = 0L,
    @ColumnInfo(name = "due_date")
    val dueDate: Long,
    // TODO: Allow users to set/change the status of a task
    @ColumnInfo(name = "status")
    val status: TaskStatus = TaskStatus.TODO,
    @ColumnInfo(name = "collection_title")
    val collectionTitle: String,
    @ColumnInfo(name = "category_title")
    val categoryTitle: String
) {
    fun toTask(): Task {
        /*val timeNow by lazy { Calendar.getInstance().timeInMillis }
        val taskStatus = when {
            dueDate > timeNow -> TaskStatus.TODO
            dueDate <= timeNow -> TaskStatus.IN_PROGRESS
            else -> TaskStatus.NONE
        }*/
        return Task(
            id = id,
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
}
