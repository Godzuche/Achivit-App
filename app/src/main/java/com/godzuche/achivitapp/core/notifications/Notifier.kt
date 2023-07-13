package com.godzuche.achivitapp.core.notifications

import com.godzuche.achivitapp.domain.model.Task

interface Notifier {
    fun postTaskNotifications(tasks: List<Task>)
}