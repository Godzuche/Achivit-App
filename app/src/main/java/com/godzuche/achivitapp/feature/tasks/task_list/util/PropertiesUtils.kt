package com.godzuche.achivitapp.feature.tasks.task_list.util

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.godzuche.achivitapp.feature.tasks.task_list.TaskListAdapter

data class Icons(
    val deleteIcon: Drawable?,
    val snoozeIcon: Drawable?,
)

data class Measurements(
    val height: Int,
    val width: Int,
)

data class RvColors(
    val deleteColor: Int,
    val deleteColorDark: Int,
    val snoozeColor: Int,
    val snoozeColorDark: Int,
)

data class ViewUtil(
    val layoutManager: LinearLayoutManager,
    val adapter: TaskListAdapter,
    val requiredView: View,
    val anchorView: View?,
    val context: Context,
    val resources: Resources,
)