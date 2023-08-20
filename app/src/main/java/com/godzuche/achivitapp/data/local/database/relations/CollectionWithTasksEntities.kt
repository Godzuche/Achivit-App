package com.godzuche.achivitapp.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.data.local.database.model.TaskEntity
import com.godzuche.achivitapp.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.domain.model.CollectionWithTasks

data class CollectionWithTasksEntities(
    @Embedded val collection: TaskCollectionEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "collection_title"
    )
    val tasks: List<TaskEntity>,
//    val tasks: PagingSource<Int, TaskEntity>,
)

fun CollectionWithTasksEntities.asExternalModel() = CollectionWithTasks(
    collection = collection.asExternalModel(),
    tasks = tasks.map(TaskEntity::asExternalModel)
)


/*
@DatabaseView(
    "SELECT task_collections.*, task_table.*" +
            "FROM task_collections " +
            "INNER JOIN task_table ON task_collections.title = task_table.collection_title"
)
data class CollectionWithTasksEntities(
    @Embedded val collection: TaskCollectionEntity,
    @Embedded val task: TaskEntity
)*/
