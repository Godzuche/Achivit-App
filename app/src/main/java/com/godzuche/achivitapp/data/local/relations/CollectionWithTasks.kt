package com.godzuche.achivitapp.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.data.local.entity.TaskEntity

data class CollectionWithTasks(
    @Embedded val collection: TaskCollectionEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "collection_title"
    )
    val tasks: List<TaskEntity>,
//    val tasks: PagingSource<Int, TaskEntity>,
)