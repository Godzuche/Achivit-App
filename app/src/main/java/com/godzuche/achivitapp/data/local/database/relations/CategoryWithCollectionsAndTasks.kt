package com.godzuche.achivitapp.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.godzuche.achivitapp.data.local.database.model.TaskCategoryEntity
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity

/**
 * A model of nested relationship for one to many relationship
 * between [TaskCategoryEntity] entity class and [CollectionWithTasks] relationship class.
 */
data class CategoryWithCollectionsAndTasks(
    @Embedded val category: TaskCategoryEntity,
    @Relation(
        parentColumn = "title",
        entityColumn = "category_title",
        entity = TaskCollectionEntity::class
    )
    val collectionWithTasks: List<CollectionWithTasks>
)