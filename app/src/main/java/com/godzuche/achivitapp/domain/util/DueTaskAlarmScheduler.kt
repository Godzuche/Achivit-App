package com.godzuche.achivitapp.domain.util

import com.godzuche.achivitapp.domain.model.Task

interface DueTaskAlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}