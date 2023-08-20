package com.godzuche.achivitapp.domain.repository

import android.net.Uri
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.UserData
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun requestOneTapSignIn(): Flow<AchivitResult<BeginSignInResult>>

    fun googleSignInWithCredential(credential: SignInCredential): Flow<AchivitResult<UserData?>>

    fun signOut(): Flow<AchivitResult<Nothing?>>

    fun getSignedInUser(): Flow<AchivitResult<UserData?>>

    fun updateUserProfile(photoUri: Uri, displayName: String): Flow<AchivitResult<Nothing?>>
}