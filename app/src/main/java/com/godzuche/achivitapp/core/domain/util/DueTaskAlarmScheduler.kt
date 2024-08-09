package com.godzuche.achivitapp.core.domain.util

import com.godzuche.achivitapp.core.domain.model.Task

interface DueTaskAlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}