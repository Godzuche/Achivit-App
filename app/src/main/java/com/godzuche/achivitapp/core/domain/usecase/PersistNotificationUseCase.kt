package com.godzuche.achivitapp.core.domain.usecase

import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.model.toNotification
import com.godzuche.achivitapp.core.domain.repository.NotificationRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class PersistNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
) {
    suspend operator fun invoke(task: Task) {
        val notification = task
            .toNotification()
            .copy(isRead = false, date = Clock.System.now())

        notificationRepository.insertOrReplaceNotification(notification)
    }

}