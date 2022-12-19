package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_categories")
data class TaskCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "created")
    val created: Long
) {
    fun toTaskCategory() = TaskCategory(title = title, created = created)
}

data class TaskCategory(val title: String, val created: Long) {
    fun toCategoryEntity() = TaskCategoryEntity(title = title, created = created)
}