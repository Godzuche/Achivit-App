package com.godzuche.achivitapp.core.notifications

import com.godzuche.achivitapp.core.domain.model.Task

interface Notifier {
    fun postTaskNotifications(tasks: List<Task>)
}