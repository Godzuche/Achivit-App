package com.godzuche.achivitapp.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity

data class CategoryWithCollections(
    @Embedded val category: TaskCategoryEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "category_title"
    )
    val collections: List<TaskCollectionEntity>,
)