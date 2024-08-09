package com.godzuche.achivitapp.core.domain.model

data class CollectionWithTasks(
    val collection: com.godzuche.achivitapp.core.domain.model.TaskCollection,
    val tasks: List<com.godzuche.achivitapp.core.domain.model.Task>,
)
