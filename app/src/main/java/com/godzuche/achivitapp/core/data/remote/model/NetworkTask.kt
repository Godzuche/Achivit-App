package com.godzuche.achivitapp.core.data.remote.model

import com.godzuche.achivitapp.core.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.core.domain.model.TaskStatus

data class NetworkTask(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean = false,
    val created: Long,
    val dueDate: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val collectionTitle: String,
    val categoryTitle: String,
) {
    // A no-argument default constructor, which is needed for deserialization from a DocumentSnapshot.
    // Required to call DocumentSnapshot.toObject(Class<T>) and DocumentSnapshot.toObjects(Class<T>)

    // Not needed if we use default values for the fields in the data class because Kotlin automatically
    // generates a no-argument constructor.
    @Suppress("unused")
    constructor() : this(0, "", "", false, 0L, 0L, TaskStatus.TODO, "", "")
}

fun NetworkTask.asEntity() = TaskEntity(
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