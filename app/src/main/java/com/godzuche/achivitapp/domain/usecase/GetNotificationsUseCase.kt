package com.godzuche.achivitapp.domain.usecase

import com.godzuche.achivitapp.domain.model.Notification
import com.godzuche.achivitapp.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> =
        notificationRepository.getNotifications()
}
