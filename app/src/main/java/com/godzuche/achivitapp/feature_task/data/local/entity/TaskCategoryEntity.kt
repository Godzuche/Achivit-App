package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_categories")
data class TaskCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
) {
    fun toTaskCategory() = TaskCategory(title = title)
}

data class TaskCategory(val title: String) {
    fun toCategoryEntity() = TaskCategoryEntity(title = title)
}