package com.godzuche.achivitapp.data.repository

import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.domain.model.UserData
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun requestOneTapSignIn(): Flow<AchivitResult<BeginSignInResult>>

    suspend fun googleSignInWithCredential(credential: SignInCredential): Flow<AchivitResult<UserData?>>

    suspend fun signOut(): Flow<AchivitResult<Nothing?>>

    fun getSignedInUser(): Flow<AchivitResult<UserData?>>
}