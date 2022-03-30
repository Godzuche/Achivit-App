package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.feature_task.domain.model.Task


@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "completed")
    val completed: Boolean = false,
/*    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "time")
    val time: Long*/
//    @ColumnInfo(name = "status")
//    val status: Status
) {

    /*    data class Status(

        )*/
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            completed = completed,
/*            date = date,
            time = time*/
        )
    }
}
