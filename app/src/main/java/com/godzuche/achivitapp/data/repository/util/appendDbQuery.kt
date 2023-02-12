package com.godzuche.achivitapp.data.repository.util

fun appendDbQuery(query: String): String {
    return "%${query.replace(' ', '%')}%"
}