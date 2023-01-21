package com.godzuche.achivitapp.core.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.core.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.entity.TaskCollectionEntity

data class CategoryWithCollections(
    @Embedded val category: TaskCategoryEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "category_title"
    )
    val collections: List<TaskCollectionEntity>,
)
