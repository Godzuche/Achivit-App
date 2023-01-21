package com.godzuche.achivitapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_collections")
data class TaskCollectionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "category_title")
    val categoryTitle: String,
) {
    fun toTaskCollection() = TaskCollection(title = title, categoryTitle = categoryTitle)
}

data class TaskCollection(
    val title: String,
    val categoryTitle: String
) {
    fun toTaskCollectionEntity() =
        TaskCollectionEntity(title = title, categoryTitle = categoryTitle)
}