package com.godzuche.achivitapp.feature_home.presentation

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import com.godzuche.achivitapp.core.util.capitalizeEachWord
import com.godzuche.achivitapp.feature_tasks_feed.presentation.util.TaskStatus

data class TaskStatusOverview(
    val taskCount: Int,
    val status: String,
    val taskColor: Color,
    val statusIcon: Icon? = null
)

val TASK_STATUS_OVERVIEWS = listOf(
    TaskStatusOverview(
        taskCount = 12,
        status = TaskStatus.TODO.name.lowercase().replaceFirstChar { it.uppercase() },
        taskColor = Color.Gray
    ),
    TaskStatusOverview(
        taskCount = 8,
        status = TaskStatus.IN_PROGRESS.name.capitalizeEachWord(),
        taskColor = Color(0xFFFFA500)
    ),
    TaskStatusOverview(
        taskCount = 4,
        status = TaskStatus.RUNNING_LATE.name.capitalizeEachWord(),
        taskColor = Color.Red
    ),
    TaskStatusOverview(
        taskCount = 27,
        status = TaskStatus.COMPLETED.name.lowercase().replaceFirstChar { it.uppercase() },
        taskColor = Color(0xFF52D726)
    )
)