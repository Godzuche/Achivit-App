package com.godzuche.achivitapp.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_fts")
@Fts4(contentEntity = TaskEntity::class)
data class TaskFtsEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val taskId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String
)
