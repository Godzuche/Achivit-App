package com.godzuche.achivitapp.feature.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.domain.repository.NotificationRepository
import com.godzuche.achivitapp.core.domain.usecase.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    getNotificationsUseCase: GetNotificationsUseCase,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val notificationUiState: StateFlow<NotificationUiState> =
        getNotificationsUseCase().map(NotificationUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NotificationUiState.Loading
            )

    fun readNotification(notification: com.godzuche.achivitapp.core.domain.model.Notification) {
        viewModelScope.launch {
            notificationRepository.readNotification(notification)
        }
    }

    val notificationsCountState: StateFlow<Int> =
        notificationRepository
            .getNotificationsCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = 0
            )

}

sealed interface NotificationUiState {
    object Loading : NotificationUiState

    data class Success(
        val notifications: List<com.godzuche.achivitapp.core.domain.model.Notification>
    ) : NotificationUiState
}