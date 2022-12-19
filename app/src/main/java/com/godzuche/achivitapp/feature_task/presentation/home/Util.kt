package com.godzuche.achivitapp.feature_task.presentation.home

import java.text.SimpleDateFormat
import java.util.*

fun Long.millisToString(pattern: String = "E, MMM d, h:mm a"): String {
    val calender = Calendar.getInstance()
    val timeMillis = this
    calender.timeInMillis = timeMillis
    val date = calender.time
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}