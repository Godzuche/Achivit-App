package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_categories")
data class TaskCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
) {
    fun toCategory(): Category {
        return Category(title = title)
    }
}

data class Category(
    val id: Long? = null,
    val title: String,
) {
    fun toNewTaskCategoryEntity(): TaskCategoryEntity {
        return TaskCategoryEntity(title = title)
    }

    fun toTaskCategoryEntity(): TaskCategoryEntity {
        return TaskCategoryEntity(
            categoryId = id!!,
            title = title
        )
    }
}