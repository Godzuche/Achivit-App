package com.godzuche.achivitapp.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.domain.repository.DarkThemeConfig
import com.godzuche.achivitapp.domain.repository.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData.map { userData ->
            SettingsUiState.Success(
                settings = UserEditableSettings(
                    darkThemeConfig = userData.darkThemeConfig
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState.Loading
        )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

}

sealed interface SettingsUiState {
    object Loading : SettingsUiState

    data class Success(val settings: UserEditableSettings) : SettingsUiState
}

data class UserEditableSettings(
    val darkThemeConfig: DarkThemeConfig
)