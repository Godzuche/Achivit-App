package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus


@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
/*    @ColumnInfo(name = "completed")
    val completed: Boolean = false,*/
    /*@ColumnInfo(name = "isPriority")
    val isPriority: Boolean = false,*/
    @ColumnInfo(name = "created")
    val created: Long = 0L,
    @ColumnInfo(name = "due_date")
    val dueDate: Long,
    @ColumnInfo(name = "status")
    val status: TaskStatus = TaskStatus.TODO,
    @ColumnInfo(name = "collection_title")
    val collectionTitle: String,
) {

    /*    data class Status(

        )*/
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            created = created,
            dueDate = dueDate,
            status = status,
            collectionTitle = collectionTitle
        )
    }
}
