package com.godzuche.achivitapp.core.ui.util

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 * This removes the content padding width constraint defined by the parent layout.
 * It enables edge-to-edge scrolling horizontally
 * */
fun Modifier.removeWidthConstraint(contentPadding: Dp) =
    this.layout { measurable, constraints ->
        val placeable: Placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + (contentPadding * 2).roundToPx()
            )
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

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