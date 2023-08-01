package com.godzuche.achivitapp.data.repository

import android.net.Uri
import com.godzuche.achivitapp.BuildConfig
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.domain.model.ExternalUserData
import com.godzuche.achivitapp.domain.model.UserData
import com.godzuche.achivitapp.domain.model.toNewExternalUserData
import com.godzuche.achivitapp.domain.model.toUserData
import com.godzuche.achivitapp.domain.repository.AuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    private val firestoreDb: FirebaseFirestore
) : AuthRepository {

    private val _isNewUser = MutableStateFlow(false)
    val isNewUser = _isNewUser.asStateFlow()

    override suspend fun requestOneTapSignIn(): Flow<AchivitResult<BeginSignInResult>> =
        flow {
            // Try launching the One Tap sign-in flow
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
                    // New user because this is sign-up flow
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

                val authResult = googleIdToken?.let { idToken ->
                    val googleAuthCredentials = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(googleAuthCredentials).await()
                }
                val firebaseUser = authResult?.user

                _isNewUser.update { authResult?.additionalUserInfo?.isNewUser == true }

                // Add user to Cloud Firestore db only after completed sign up
                if (isNewUser.value) {
                    addUserToFirestore()
                }

                val userDocRef = firebaseUser?.let {
                    firestoreDb.collection(USERS_PATH).document(firebaseUser.uid)
                }
                val firestoreUser = userDocRef?.get()?.await()?.toObject<ExternalUserData>()

                Timber.tag("DefaultAuthRepository")
                    .d("Firestore user creationDate: ${firestoreUser?.createdDate?.toDate()?.time?.millisToString()}")
                emit(
                    AchivitResult.Success(
                        data = firestoreUser?.toUserData()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(AchivitResult.Error(exception = e))
            }
        }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.let { firebaseUser ->

            val userData = firebaseUser.toNewExternalUserData()

            firestoreDb.collection(USERS_PATH)
                .document(userData.userId)
                .set(userData)
                .await()
        }
    }

    override suspend fun signOut(): Flow<AchivitResult<Nothing?>> = flow {
        try {
            emit(AchivitResult.Loading)
            oneTapClient.signOut().await()
//            auth.currentUser?.unlink(GoogleAuthProvider.PROVIDER_ID)
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
            val userDocRef = signedInUser?.let {
                firestoreDb.collection(USERS_PATH).document(it.uid)
            }
            val firestoreUser = userDocRef?.get()?.await()?.toObject<ExternalUserData>()

            if (isUserSignedIn) {
                emit(
                    AchivitResult.Success(
                        data = firestoreUser?.toUserData()
                    )
                )
            } else {
                emit(AchivitResult.Success(data = null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }
    }

    override suspend fun updateUserProfile(
        photoUri: Uri,
        displayName: String
    ): Flow<AchivitResult<Nothing?>> = flow {
        try {
            emit(AchivitResult.Loading)
            val profileUpdates = userProfileChangeRequest {
                this.photoUri = photoUri
                this.displayName = displayName
            }
            auth.currentUser!!.updateProfile(profileUpdates).await()
            emit(AchivitResult.Success(data = null))
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

const val USERS_PATH = "users"