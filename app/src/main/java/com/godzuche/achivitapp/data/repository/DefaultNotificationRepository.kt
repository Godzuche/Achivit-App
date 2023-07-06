package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.data.local.database.dao.NotificationDao
import com.godzuche.achivitapp.data.local.database.model.toNotificationEntity
import com.godzuche.achivitapp.domain.model.Notification
import com.godzuche.achivitapp.domain.model.asExternalModel
import com.godzuche.achivitapp.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultNotificationRepository @Inject constructor(
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getNotifications(): Flow<List<Notification>> =
        notificationDao.getNotificationEntities().map { notifications ->
            notifications.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceNotification(notification: Notification) {
        withContext(ioDispatcher) {
            notificationDao.insertOrReplaceNotification(
                notification.toNotificationEntity()
            )
        }
    }

    override suspend fun readNotification(notification: Notification) {
        withContext(ioDispatcher) {
            notificationDao.insertOrReplaceNotification(
                notification
                    .copy(isRead = true)
                    .toNotificationEntity()
            )
        }
    }

    override fun getNotificationsCount(): Flow<Int> =
        notificationDao.getCount()

    override suspend fun deleteNotification(notification: Notification) {
        withContext(ioDispatcher) {
            notificationDao.delete(notification.toNotificationEntity())
        }
    }
}