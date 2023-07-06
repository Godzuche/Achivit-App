package com.godzuche.achivitapp.domain.model

import com.godzuche.achivitapp.data.local.database.model.NotificationEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Notification(
    val id: Int,
    val title: String,
    val content: String,
    val isRead: Boolean,
    val date: Instant = Clock.System.now()
)

fun NotificationEntity.asExternalModel() = Notification(
    id = id,
    title = title,
    content = content,
    isRead = isRead
)

fun Task.toNotification() = Notification(
    id = id!!,
    title = title,
    content = description,
    isRead = false
)