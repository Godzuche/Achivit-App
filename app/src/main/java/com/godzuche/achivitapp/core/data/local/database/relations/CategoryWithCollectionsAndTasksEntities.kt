package com.godzuche.achivitapp.core.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.core.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.core.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.core.data.local.database.model.asExternalModel
import com.godzuche.achivitapp.core.domain.model.CategoryWithCollectionsAndTasks

/**
 * A model of nested relationship for one to many relationship
 * between [TaskCategoryEntity] entity class and [CollectionWithTasksEntities] relationship class.
 */
data class CategoryWithCollectionsAndTasksEntities(
    @Embedded val category: TaskCategoryEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "category_title",
        entity = TaskCollectionEntity::class
    )
    val collectionWithTasks: List<CollectionWithTasksEntities>
)

fun CategoryWithCollectionsAndTasksEntities.asExternalModel() =
    com.godzuche.achivitapp.core.domain.model.CategoryWithCollectionsAndTasks(
        category = category.asExternalModel(),
        collectionWithTasks = collectionWithTasks.map(CollectionWithTasksEntities::asExternalModel)
    )