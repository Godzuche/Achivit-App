package com.godzuche.achivitapp.feature.home.presentation.core.util

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt


val Int.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()
