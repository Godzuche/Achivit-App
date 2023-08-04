package com.godzuche.achivitapp.domain.util

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting app connectivity status
 */
interface NetworkMonitor {
    val isOnline: Boolean
    val isOnlineFlow: Flow<Boolean>
}