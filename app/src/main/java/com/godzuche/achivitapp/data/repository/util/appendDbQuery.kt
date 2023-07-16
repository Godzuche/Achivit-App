package com.godzuche.achivitapp.data.repository.util

/*fun appendDbQuery(query: String): String {
    return "%${query.replace(' ', '%')}%"
}*/

fun appendFtsDbQuery(query: String) =
    "*${query/*.replace(' ', '*')*/}*"