package com.godzuche.achivitapp.feature.home.presentation.core.util

fun String.capitalizeEachWord(): String = split("_").joinToString(" ") {
    it.lowercase().replaceFirstChar { char -> char.uppercase() }
}