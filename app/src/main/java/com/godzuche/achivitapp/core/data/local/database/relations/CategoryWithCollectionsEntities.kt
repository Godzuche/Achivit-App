package com.godzuche.achivitapp.core.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.core.domain.model.CategoryWithCollections

data class CategoryWithCollectionsEntities(
    @Embedded val category: TaskCategoryEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "category_title"
    )
    val collections: List<TaskCollectionEntity>,
)

fun CategoryWithCollectionsEntities.asExternalModel() =
    com.godzuche.achivitapp.core.domain.model.CategoryWithCollections(
        category = category.asExternalModel(),
        collections = collections.map(TaskCollectionEntity::asExternalModel)
    )