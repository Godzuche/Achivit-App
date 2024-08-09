package com.godzuche.achivitapp.core.domain.model

data class CategoryWithCollections(
    val category: com.godzuche.achivitapp.core.domain.model.TaskCategory,
    val collections: List<com.godzuche.achivitapp.core.domain.model.TaskCollection>,
)
