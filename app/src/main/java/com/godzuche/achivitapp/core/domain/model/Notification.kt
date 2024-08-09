package com.godzuche.achivitapp.core.domain.model

import com.godzuche.achivitapp.core.data.local.database.model.NotificationEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Notification(
    val id: Int,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val date: Instant = Clock.System.now()
)

fun NotificationEntity.asExternalModel() = com.godzuche.achivitapp.core.domain.model.Notification(
    id = id,
    title = title,
    content = content,
    isRead = isRead
)

fun com.godzuche.achivitapp.core.domain.model.Task.toNotification() =
    com.godzuche.achivitapp.core.domain.model.Notification(
        id = id!!,
        title = title,
        content = description,
        isRead = false
    )