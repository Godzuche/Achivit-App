package com.godzuche.achivitapp.feature_home.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_home.data.local.entity.TaskEntity

data class CollectionWithTasks(
    @Embedded val collection: TaskCollectionEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "collection_title"
    )
    val tasks: List<TaskEntity>,
)
