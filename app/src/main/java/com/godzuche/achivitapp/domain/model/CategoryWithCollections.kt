package com.godzuche.achivitapp.domain.model

data class CategoryWithCollections(
    val category: TaskCategory,
    val collections: List<TaskCollection>,
)
