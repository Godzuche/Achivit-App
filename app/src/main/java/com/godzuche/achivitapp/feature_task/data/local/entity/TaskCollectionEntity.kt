package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_collections")
data class TaskCollectionEntity(
/*    @PrimaryKey(autoGenerate = true)
    val collectionId: Int = 0,*/
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category_title")
    val categoryTitle: String,
) {
/*    fun toCollection(): TaskCollection {
        return TaskCollection(title = title)
    }*/
}
/*
data class TaskCollection(
    val id: Int? = null,
    val title: String,
) {
    fun toNewTaskCollectionEntity(): TaskCollectionEntity {
        return TaskCollectionEntity(title = title)
    }

    fun toTaskCollectionEntity(): TaskCollectionEntity {
        return TaskCollectionEntity(
            collectionId = id!!,
            title = title
        )
    }
}*/
