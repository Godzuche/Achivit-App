package com.godzuche.achivitapp.core.data.local.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.godzuche.achivitapp.core.data.local.database.util.DatabaseConstants
import com.godzuche.achivitapp.core.data.remote.model.NetworkTask
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.TaskStatus

/**
 * Defines a database entity that stores task.
 *It has one to many relationship with [TaskCollectionEntity]
 */
@Entity(
    tableName = DatabaseConstants.TASK_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = TaskCollectionEntity::class,
            parentColumns = ["title"],
            childColumns = ["collection_title"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["title"]),
        Index(value = ["category_title"]),
        Index(value = ["collection_title"]),
        Index(value = ["due_date"]),
        Index(value = ["status"]),
        Index(value = ["description"])
    ]
)

data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "completed")
    val completed: Boolean = false,
    @ColumnInfo(name = "created")
    val created: Long = 0L,
    @ColumnInfo(name = "due_date")
    val dueDate: Long,
    @ColumnInfo(name = "status")
    val status: TaskStatus = TaskStatus.TODO,
    @ColumnInfo(name = "collection_title")
    val collectionTitle: String,
    @ColumnInfo(name = "category_title")
    val categoryTitle: String
)

fun TaskEntity.asExternalModel() = Task(
    id = id,
    title = title,
    description = description,
    isCompleted = completed,
    created = created,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)

fun TaskEntity.asNetworkModel() = NetworkTask(
    id = id,
    title = title,
    description = description,
    completed = completed,
    created = created,
    dueDate = dueDate,
    status = status,
    collectionTitle = collectionTitle,
    categoryTitle = categoryTitle
)