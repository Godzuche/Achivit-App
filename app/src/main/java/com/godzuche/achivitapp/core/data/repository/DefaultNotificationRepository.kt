package com.godzuche.achivitapp.core.data.repository

import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.data.local.database.dao.NotificationDao
import com.godzuche.achivitapp.core.data.local.database.model.toNotificationEntity
import com.godzuche.achivitapp.core.domain.model.Notification
import com.godzuche.achivitapp.core.domain.model.asExternalModel
import com.godzuche.achivitapp.core.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultNotificationRepository @Inject constructor(
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getNotifications(): Flow<List<com.godzuche.achivitapp.core.domain.model.Notification>> =
        notificationDao.getNotificationEntities().map { notifications ->
            notifications.map { it.asExternalModel() }
        }

    override suspend fun insertOrReplaceNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification) {
        withContext(ioDispatcher) {
            notificationDao.insertOrReplaceNotification(
                notification.toNotificationEntity()
            )
        }
    }

    override suspend fun readNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification) {
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

    override suspend fun deleteNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification) {
        withContext(ioDispatcher) {
            notificationDao.delete(notification.toNotificationEntity())
        }
    }
}