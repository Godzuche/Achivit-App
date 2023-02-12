package com.godzuche.achivitapp.feature_home.presentation.core.util

fun String.capitalizeEachWord(): String = split("_").joinToString(" ") {
    it.lowercase().replaceFirstChar { char -> char.uppercase() }
}