package com.godzuche.achivitapp.feature_task.domain

import com.godzuche.achivitapp.feature_task.domain.model.Task

interface DueTaskAlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}