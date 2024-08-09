package com.godzuche.achivitapp.core.domain.usecase

import com.godzuche.achivitapp.core.domain.model.Notification
import com.godzuche.achivitapp.core.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<com.godzuche.achivitapp.core.domain.model.Notification>> =
        notificationRepository.getNotifications()
}
