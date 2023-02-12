package com.godzuche.achivitapp.domain

import com.godzuche.achivitapp.domain.model.Task

interface DueTaskAlarmScheduler {
    fun schedule(task: Task)
    fun cancel(task: Task)
}