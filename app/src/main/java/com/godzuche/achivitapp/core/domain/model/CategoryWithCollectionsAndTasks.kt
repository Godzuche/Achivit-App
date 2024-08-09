package com.godzuche.achivitapp.core.domain.model

data class CategoryWithCollectionsAndTasks(
    val category: com.godzuche.achivitapp.core.domain.model.TaskCategory,
    val collectionWithTasks: List<com.godzuche.achivitapp.core.domain.model.CollectionWithTasks>,
)
