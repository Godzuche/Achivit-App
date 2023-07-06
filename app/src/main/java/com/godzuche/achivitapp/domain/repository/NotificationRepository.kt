package com.godzuche.achivitapp.domain.repository

import com.godzuche.achivitapp.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<Notification>>

    suspend fun insertOrReplaceNotification(notification: Notification)

    suspend fun readNotification(notification: Notification)

    fun getNotificationsCount(): Flow<Int>

    suspend fun deleteNotification(notification: Notification)
}