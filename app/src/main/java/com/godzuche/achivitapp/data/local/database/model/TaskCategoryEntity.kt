package com.godzuche.achivitapp.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.data.remote.model.NetworkTaskCategory
import com.godzuche.achivitapp.domain.model.TaskCategory

/**
 * Defines a database entity that stores category of collections.
 *It has one to many relationship with [TaskCollectionEntity]
 */
@Entity(tableName = "task_categories")
data class TaskCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "created")
    val created: Long
)

fun TaskCategoryEntity.asExternalModel() = TaskCategory(title = title, created = created)

fun TaskCategoryEntity.asNetworkModel() = NetworkTaskCategory(title = title, created = created)