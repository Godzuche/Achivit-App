package com.godzuche.achivitapp.core.domain.model

data class TaskFilter(
    val category: TaskCategory? = null,
    val collection: TaskCollection? = null,
    val status: TaskStatus = TaskStatus.NONE,
)
