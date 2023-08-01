package com.godzuche.achivitapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.UserData
import com.godzuche.achivitapp.domain.repository.AuthRepository
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authUiState: MutableStateFlow<AuthUiState> =
        MutableStateFlow(AuthUiState.NotLoading)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val userAuthState: StateFlow<UserAuthState> =
        authRepository.getSignedInUser().flatMapLatest { result ->
            when (result) {
                AchivitResult.Loading -> {
                    flowOf(UserAuthState.Loading)
                }

                is AchivitResult.Success -> {
                    val userData = result.data
                    val isUserSignedIn = (userData.isNotNull())
                    if (isUserSignedIn) {
                        flowOf(UserAuthState.SignedIn(data = userData!!))
                    } else {
                        flowOf(UserAuthState.NotSignedIn)
                    }
                }

                is AchivitResult.Error -> {
                    flowOf(UserAuthState.Error(e = result.exception))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserAuthState.Loading
        )

    fun requestOneTapSignIn() {
        viewModelScope.launch {
            authRepository.requestOneTapSignIn().onEach { result ->
                when (result) {
                    AchivitResult.Loading -> {
                        _authUiState.update { AuthUiState.Loading }
                    }

                    is AchivitResult.Success -> {
                        _authUiState.update {
                            AuthUiState.OneTapUi(data = result.data)
                        }
                    }

                    is AchivitResult.Error -> {
                        _authUiState.update {
                            AuthUiState.Error(exception = result.exception)
                        }
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun signInWithGoogle(getSignInCredential: () -> SignInCredential) {
        viewModelScope.launch(Dispatchers.IO) {
            val signInCredential: SignInCredential? = try {
                getSignInCredential()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                _authUiState.update { AuthUiState.Error(exception = e) }
                null
            }

            signInCredential?.let {
                authRepository.googleSignInWithCredential(credential = signInCredential)
                    .onEach { result ->
                        when (result) {
                            is AchivitResult.Loading -> {
                                _authUiState.update {
                                    AuthUiState.Loading
                                }
                            }

                            is AchivitResult.Error -> {
                                _authUiState.update {
                                    AuthUiState.Error(exception = result.exception)
                                }
                            }

                            is AchivitResult.Success -> {
                                _authUiState.update {
                                    AuthUiState.Success(data = result.data)
                                }
                            }
                        }
                    }.launchIn(viewModelScope)
            }
        }
    }


}

sealed interface AuthUiState {
    object NotLoading : AuthUiState
    object Loading : AuthUiState
    data class OneTapUi<T>(val data: T) : AuthUiState
    data class Success(val data: UserData?) : AuthUiState
    data class Error(val exception: Throwable? = null) : AuthUiState
}

sealed interface UserAuthState {
    object Loading : UserAuthState
    data class SignedIn(val data: UserData) : UserAuthState
    object NotSignedIn : UserAuthState
    data class Error(val e: Throwable?) : UserAuthState
}

fun Any?.isNotNull(): Boolean = this != null