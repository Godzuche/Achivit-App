package com.godzuche.achivitapp.core.data.repository.util

/*fun appendDbQuery(query: String): String {
    return "%${query.replace(' ', '%')}%"
}*/

fun appendFtsDbQuery(query: String) =
    "*${query/*.replace(' ', '*')*/}*"