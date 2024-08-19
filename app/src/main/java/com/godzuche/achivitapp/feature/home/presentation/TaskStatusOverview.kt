package com.godzuche.achivitapp.feature.home.presentation

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import com.godzuche.achivitapp.core.design_system.theme.Alpha
import com.godzuche.achivitapp.core.design_system.theme.CompletedColor
import com.godzuche.achivitapp.core.design_system.theme.InProgressColor
import com.godzuche.achivitapp.core.design_system.theme.NoneColor
import com.godzuche.achivitapp.core.design_system.theme.RunningLateColor
import com.godzuche.achivitapp.core.design_system.theme.TodoColor
import com.godzuche.achivitapp.core.domain.model.TaskStatus

sealed class TaskStatusOverview(
    val taskCount: Int,
    val taskStatus: TaskStatus,
    val taskColor: Color,
    val statusIcon: Icon? = null,
) {
    data class None(
        val count: Int = 0,
        val status: TaskStatus = TaskStatus.NONE,
        val color: Color = NoneColor,
        val icon: Icon? = null,
    ) : TaskStatusOverview(count, status, color, icon)

    data class Todo(
        val count: Int = 0,
        val status: TaskStatus = TaskStatus.TODO,
        val color: Color = TodoColor.copy(Alpha.MEDIUM),
        val icon: Icon? = null,
    ) : TaskStatusOverview(count, status, color, icon)

    data class InProgress(
        val count: Int = 0,
        val status: TaskStatus = TaskStatus.IN_PROGRESS,
        val color: Color = InProgressColor.copy(Alpha.MEDIUM),
        val icon: Icon? = null,
    ) : TaskStatusOverview(count, status, color, icon)

    data class RunningLate(
        val count: Int = 0,
        val status: TaskStatus = TaskStatus.RUNNING_LATE,
        val color: Color = RunningLateColor.copy(Alpha.MEDIUM),
        val icon: Icon? = null,
    ) : TaskStatusOverview(count, status, color, icon)

    data class Completed(
        val count: Int = 0,
        val status: TaskStatus = TaskStatus.COMPLETED,
        val color: Color = CompletedColor.copy(Alpha.MEDIUM),
        val icon: Icon? = null,
    ) : TaskStatusOverview(count, status, color, icon)
}