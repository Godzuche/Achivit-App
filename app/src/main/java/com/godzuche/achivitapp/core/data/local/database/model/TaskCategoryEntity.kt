package com.godzuche.achivitapp.core.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.data.remote.model.NetworkTaskCategory
import com.godzuche.achivitapp.core.domain.model.TaskCategory

/**
 * Defines a database entity that stores category of collections.
 *It has one to many relationship with [TaskCollectionEntity]
 */
@Entity(tableName = DatabaseConstants.CATEGORY_TABLE_NAME)
data class TaskCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "created")
    val created: Long,
) {
    companion object {
        const val COLUMN_TITLE = "title"
        const val COLUMN_CREATED = "created"
    }
}

fun TaskCategoryEntity.asExternalModel() = TaskCategory(title = title, created = created)

fun TaskCategoryEntity.asNetworkModel() = NetworkTaskCategory(title = title, created = created)