package com.godzuche.achivitapp.feature.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.repository.AuthRepository
import com.godzuche.achivitapp.feature.auth.UserAuthState
import com.godzuche.achivitapp.feature.auth.isNotNull
import com.godzuche.achivitapp.feature.tasks.task_list.AchivitDialog
import com.godzuche.achivitapp.feature.tasks.task_list.DialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState = _dialogState.asStateFlow()

    private val _uiState: MutableStateFlow<ProfileUiState> =
        MutableStateFlow(ProfileUiState.Success())
    val uiState = _uiState.asStateFlow()

    private val _userAuthState: MutableStateFlow<UserAuthState> =
        MutableStateFlow(UserAuthState.Loading)

    val userAuthState: StateFlow<UserAuthState> = _userAuthState.asStateFlow()

    init {
        getSignedInUser()
    }

    private fun getSignedInUser() {
        authRepository.getSignedInUser().onEach { result ->
            when (result) {
                AchivitResult.Loading -> {
                    _userAuthState.update { UserAuthState.Loading }
                }

                is AchivitResult.Success -> {
                    val userData = result.data
                    val isUserSignedIn = (userData.isNotNull())
                    if (isUserSignedIn) {
                        _userAuthState.update { UserAuthState.SignedIn(data = userData!!) }
                    } else {
                        _userAuthState.update { UserAuthState.NotSignedIn }
                    }
                }

                is AchivitResult.Error -> {
                    _userAuthState.update { UserAuthState.Error(e = result.exception) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateUserProfile(photoUri: Uri, displayName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.updateUserProfile(photoUri, displayName).onEach { result ->
                when (result) {
                    AchivitResult.Loading -> {
                        _uiState.update { ProfileUiState.Loading }
                    }

                    is AchivitResult.Success -> {
                        _uiState.update {
                            ProfileUiState.Success(message = "Profile updated successfully! :)")
                        }
                    }

                    is AchivitResult.Error -> {
                        _uiState.update { ProfileUiState.Error(result.exception) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun signOut() {
        viewModelScope.launch {

            authRepository.signOut().onEach { result ->
                when (result) {
                    AchivitResult.Loading -> {
                        _uiState.update { ProfileUiState.Loading }
//                        _userAuthState.update { UserAuthState.Loading }
                    }

                    is AchivitResult.Success -> {
                        _userAuthState.update { UserAuthState.NotSignedIn }
//                        _uiState.update { ProfileUiState.Success(result.data) }
                    }

                    is AchivitResult.Error -> {
//                        _userAuthState.update { UserAuthState.Error(e = result.exception) }
                        _uiState.update { ProfileUiState.Error(exception = result.exception) }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun setDialogState(shouldShow: Boolean, dialog: AchivitDialog? = null) {
        _dialogState.update {
            it.copy(shouldShow = shouldShow, dialog = dialog)
        }
    }
}

sealed interface ProfileUiState {
    object Loading : ProfileUiState

    data class Success(val message: String? = null) : ProfileUiState

    data class Error(val exception: Throwable?) : ProfileUiState
}