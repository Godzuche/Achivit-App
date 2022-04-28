package com.godzuche.achivitapp.feature_task.receivers

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.task_details.TaskDetailFragmentArgs
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_DESC
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TASK_ID
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TITLE
import com.godzuche.achivitapp.feature_task.receivers.Constants.NOTIFICATION_CHANNEL_DESCRIPTION
import com.godzuche.achivitapp.feature_task.receivers.Constants.NOTIFICATION_CHANNEL_ID
import com.godzuche.achivitapp.feature_task.receivers.Constants.NOTIFICATION_CHANNEL_NAME
import com.godzuche.achivitapp.feature_task.receivers.Constants.NOTIFICATION_ID


fun makeTaskDueNotification(
    context: Context, /*task: Task*/
    taskId: Long,
    taskTitle: String,
    taskDescription: String,
) {

    //make notification channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val id = NOTIFICATION_CHANNEL_ID
        val name = NOTIFICATION_CHANNEL_NAME
        val channelDescription = NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val args = TaskDetailFragmentArgs(id = taskId).toBundle()
//    val args = task.id?.let { TaskDetailFragmentArgs(it).toBundle() }

    val pendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph_main)
        .setDestination(R.id.task_fragment)
        .setArguments(args)
        .createPendingIntent()

    val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(Constants.NOTIFICATION_TITLE)
        .setContentText("\"${taskTitle}\" is active now")
//        .setContentText("\"${task.title}\" is active now")
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(DEFAULT_ALL)
        .setColor(com.google.android.material.R.attr.colorPrimary)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setVibrate(LongArray(0))

        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText(taskDescription)
            .setBigContentTitle(taskTitle))

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun setReminder(
    app: Application,
    getApp: Application,
    taskId: Long,
    taskDueDate: Long,
    title: String,
    description: String,
) {
    val extras = Bundle().apply {
        putLong(KEY_TASK_ID, taskId)
        putString(KEY_TITLE, title)
        putString(KEY_DESC, description)
    }

    val context = getApp.applicationContext
    val alarmIntent = Intent(context, DueTaskAlarmReceiver::class.java).apply {
        putExtras(extras)
    }
    val alarmPendingIntent = PendingIntent.getBroadcast(
        context,
        taskId.toInt() + 1,
        alarmIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    AlarmManagerCompat.setExactAndAllowWhileIdle(
        alarmManager,
        AlarmManager.RTC_WAKEUP,
        taskDueDate,
        alarmPendingIntent
    ).also {
        Log.d("Reminder", "ReceiverUtils Set at due: $taskDueDate, title: $title, id: $taskId")
    }
}