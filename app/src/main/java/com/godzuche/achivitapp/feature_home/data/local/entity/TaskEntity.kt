package com.godzuche.achivitapp.feature_home.data.local.entity

import android.icu.util.Calendar
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.feature_home.domain.model.Task
import com.godzuche.achivitapp.feature_home.presentation.util.TaskStatus


val timeNow by lazy { Calendar.getInstance().timeInMillis }

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
    // TODO: Allow users to set/change the status of a task
    @ColumnInfo(name = "status")
    val status: TaskStatus = /*when {
        dueDate > timeNow -> TaskStatus.TODO
        dueDate <= timeNow -> TaskStatus.IN_PROGRESS
        else -> TaskStatus.NONE
    },*/TaskStatus.TODO,
    @ColumnInfo(name = "collection_title")
    val collectionTitle: String,
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
            created = created,
            dueDate = dueDate,
//            status = taskStatus,
            status = status,
            collectionTitle = collectionTitle
        )
    }
}
