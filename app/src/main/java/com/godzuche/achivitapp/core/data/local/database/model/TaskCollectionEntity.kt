package com.godzuche.achivitapp.core.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.domain.model.TaskCollection

/**
 * Defines a database entity that stores task collections.
 *It has one to many relationship with [TaskEntity]
 */
@Entity(
    tableName = DatabaseConstants.COLLECTION_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TaskCategoryEntity::class,
            parentColumns = ["title"],
            childColumns = ["category_title"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["title"]),
        Index(value = ["category_title"])
    ],
)
data class TaskCollectionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "category_title")
    val categoryTitle: String,
) {
    companion object {
        const val COLUMN_TITLE = "title"
        const val COLUMN_CATEGORY_TITLE = "category_title"
    }
}

fun TaskCollectionEntity.asExternalModel() =
    TaskCollection(title = title, categoryTitle = categoryTitle)

fun TaskCollectionEntity.asNetworkModel() =
    TaskCollection(title = title, categoryTitle = categoryTitle)