package com.godzuche.achivitapp.feature_task.data.repository.util

fun appendDbQuery(query: String): String {
    return "%${query.replace(' ', '%')}%"
}