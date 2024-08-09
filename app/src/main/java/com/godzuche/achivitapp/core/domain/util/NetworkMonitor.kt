package com.godzuche.achivitapp.core.domain.util

import kotlinx.coroutines.flow.Flow

/**
 * Utility for reporting app connectivity status
 */
interface NetworkMonitor {
    val isOnline: Boolean
    val isOnlineFlow: Flow<Boolean>
}