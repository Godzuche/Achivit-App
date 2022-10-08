package com.godzuche.achivitapp.core.util

fun String.capitalizeEachWord(): String = split("_").joinToString(" ") {
    it.lowercase().replaceFirstChar { char -> char.uppercase() }
}