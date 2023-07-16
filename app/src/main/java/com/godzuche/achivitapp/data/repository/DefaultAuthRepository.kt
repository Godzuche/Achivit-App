package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.BuildConfig
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient
) : AuthRepository {
    override suspend fun requestOneTapSignIn(): Flow<AchivitResult<BeginSignInResult>> =
        flow {
            try {
                emit(AchivitResult.Loading)
                val beginSignInResult = oneTapClient.beginSignIn(buildSignInRequest()).await()
                emit(AchivitResult.Success(data = beginSignInResult))
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(AchivitResult.Error(exception = e))

                // No saved credentials found. Try launching the One Tap sign-up flow
                try {
                    emit(AchivitResult.Loading)
                    val beginSignUpResult = oneTapClient.beginSignIn(buildSignUpRequest()).await()
                    emit(AchivitResult.Success(data = beginSignUpResult))
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is CancellationException) throw e
                    emit(AchivitResult.Error(exception = e))
                }
            }
        }

    override suspend fun googleSignInWithCredential(credential: SignInCredential): Flow<AchivitResult<UserData?>> =
        flow {
            emit(AchivitResult.Loading)
            try {
                val googleIdToken = credential.googleIdToken
                /*val username = credential.id
                val password = credential.password*/

                val firebaseUser = googleIdToken?.let { idToken ->
                    val googleAuthCredentials = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(googleAuthCredentials).await().user
                }

                emit(
                    AchivitResult.Success(
                        data = firebaseUser?.run {
                            UserData(
                                userId = uid,
                                displayName = displayName,
                                email = email,
                                profilePictureUrl = photoUrl?.toString()
                            )
                        }
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(AchivitResult.Error(exception = e))
            }
        }

    override suspend fun signOut(): Flow<AchivitResult<Nothing?>> = flow {
        try {
            emit(AchivitResult.Loading)
            oneTapClient.signOut().await()
            auth.signOut()
            emit(AchivitResult.Success(data = null))
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }

    }

    override fun getSignedInUser(): Flow<AchivitResult<UserData?>> = flow {
        try {
            emit(AchivitResult.Loading)
            val signedInUser = auth.currentUser
            val isUserSignedIn = (signedInUser != null)
            if (isUserSignedIn) {
                signedInUser?.run {
                    emit(
                        AchivitResult.Success(
                            data = UserData(
                                userId = uid,
                                displayName = displayName,
                                email = email,
                                profilePictureUrl = photoUrl?.toString()
                            )
                        )
                    )
                }
            } else {
                emit(AchivitResult.Success(data = null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest =
        BeginSignInRequest
            .builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(BuildConfig.CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

    private fun buildSignUpRequest(): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.CLIENT_ID)
                    .build()
            )
            .build()

}