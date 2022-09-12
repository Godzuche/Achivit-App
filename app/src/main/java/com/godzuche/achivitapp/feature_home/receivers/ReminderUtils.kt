package com.godzuche.achivitapp.feature_home.receivers

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.AlarmManagerCompat

const val NOTIFICATION_CHANNEL_NAME = "Due Tasks"
const val NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications for due tasks"
const val NOTIFICATION_CHANNEL_ID = "due_task_notification"
const val NOTIFICATION_TITLE = "Task Reminder"
const val NOTIFICATION_ID = 2

fun setReminder(
    getApp: Application,
    taskId: Int,
    taskDueDate: Long,
) {
    val extras = Bundle().apply {
        putInt(Constants.KEY_TASK_ID, taskId)
    }

    val context = getApp.applicationContext
    val alarmIntent = Intent(context, DueTaskAlarmReceiver::class.java).apply {
        putExtras(extras)
    }
    val alarmPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(
            context,
            taskId + 1,
            alarmIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getBroadcast(
            context,
            taskId + 1,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    AlarmManagerCompat.setExactAndAllowWhileIdle(
        alarmManager,
        AlarmManager.RTC_WAKEUP,
        taskDueDate,
        alarmPendingIntent
    )
}