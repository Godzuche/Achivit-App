package com.godzuche.achivitapp.core.domain.repository

import com.godzuche.achivitapp.core.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<com.godzuche.achivitapp.core.domain.model.Notification>>

    suspend fun insertOrReplaceNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification)

    suspend fun readNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification)

    fun getNotificationsCount(): Flow<Int>

    suspend fun deleteNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification)
}