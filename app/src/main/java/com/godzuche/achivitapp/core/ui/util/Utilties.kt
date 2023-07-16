package com.godzuche.achivitapp.core.ui.util

import android.content.res.Resources
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 *
 * */
fun String.capitalizeEachWord(): String = split("_").joinToString(" ") {
    it.lowercase().replaceFirstChar { char -> char.uppercase() }
}

val Int.toDp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()

/**
 * A utility function that converts time in Epoch milliseconds to formatted string using [pattern]
 */
fun Long.millisToString(pattern: String = "E, MMM d, h:mm a"): String {
    val calender = Calendar.getInstance()
    val timeMillis = this
    calender.timeInMillis = timeMillis
    val date = calender.time
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}