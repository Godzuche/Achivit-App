package com.godzuche.achivitapp.feature_task.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_collections")
data class TaskCollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val collectionId: Long = 0L,
    @ColumnInfo(name = "title")
    val title: String,
) {
    fun toCollection(): TaskCollection {
        return TaskCollection(title = title)
    }
}

data class TaskCollection(
    val id: Long? = null,
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
}
