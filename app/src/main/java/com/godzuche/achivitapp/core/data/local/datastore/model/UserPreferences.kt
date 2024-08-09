package com.godzuche.achivitapp.core.data.local.datastore.model

import com.godzuche.achivitapp.core.domain.repository.DarkThemeConfig
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val taskFilter: TaskFilter = TaskFilter(),
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val shouldHideOnboarding: Boolean = false
)

@Serializable
data class TaskFilter(
    val category: String = "My Tasks",
    val collection: String = "All Tasks",
    val status: TaskStatus = TaskStatus.NONE
)