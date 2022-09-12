package com.godzuche.achivitapp.feature_home.data.repository.util

fun appendDbQuery(query: String): String {
    return "%${query.replace(' ', '%')}%"
}