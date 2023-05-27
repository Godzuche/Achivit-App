package com.godzuche.achivitapp.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface AchivitResult<out T> {
    data class Success<T>(val data: T) : AchivitResult<T>
    data class Error(val exception: Throwable? = null) : AchivitResult<Nothing>
    object Loading : AchivitResult<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<AchivitResult<T>> {
    return this
        .map<T, AchivitResult<T>> {
            AchivitResult.Success(it)
        }
        .onStart { emit(AchivitResult.Loading) }
        .catch { emit(AchivitResult.Error(it)) }
}
