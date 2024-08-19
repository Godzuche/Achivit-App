package com.godzuche.achivitapp.core.data.repository.util

/**
 * A helper method to append placeholders to a query string.
 * Alternatively, you can use something like `query LIKE '%' || :title || '%` in DAO query methods.
 * */
//fun appendDbQuery(query: String): String {
//    return "%${query.replace(' ', '%')}%"
//}

/**
 * A helper method to append placeholders to a query string for FTS database search.
 * */
fun appendFtsDbQuery(query: String) =
    "*${query.replace(' ', '*')}*"