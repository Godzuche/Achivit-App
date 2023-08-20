package com.godzuche.achivitapp.domain.model

data class CollectionWithTasks(
    val collection: TaskCollection,
    val tasks: List<Task>,
)
