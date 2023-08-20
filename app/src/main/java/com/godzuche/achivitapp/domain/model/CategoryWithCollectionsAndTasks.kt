package com.godzuche.achivitapp.domain.model

data class CategoryWithCollectionsAndTasks(
    val category: TaskCategory,
    val collectionWithTasks: List<CollectionWithTasks>,
)
