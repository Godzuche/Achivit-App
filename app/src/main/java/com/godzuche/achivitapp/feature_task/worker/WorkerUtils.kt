package com.godzuche.achivitapp.feature_task.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.navigation.NavDeepLinkBuilder
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.worker.Constants.NOTIFICATION_CHANNEL_DESCRIPTION
import com.godzuche.achivitapp.feature_task.worker.Constants.NOTIFICATION_CHANNEL_ID
import com.godzuche.achivitapp.feature_task.worker.Constants.NOTIFICATION_CHANNEL_NAME
import com.godzuche.achivitapp.feature_task.worker.Constants.NOTIFICATION_TITLE


fun makeDailyTaskNotification(context: Context, tasks: List<Task>) {
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
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

    //
    val notificationStyle = NotificationCompat.InboxStyle()
        .setBigContentTitle("Current Due Tasks")
        .setSummaryText("Due Tasks Reminder")

    if (tasks.isNotEmpty()) {
        tasks.forEach {
            notificationStyle.addLine(it.title)
        }
    } else {
        return
    }

    // TODO: I will pass order by high priority of task for the particular day through args
    val pendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.nav_graph_main)
        .setDestination(R.id.action_home)
        .createPendingIntent()

    val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("You have ${tasks.size} tasks of high priority for today")
        .setContentIntent(pendingIntent)
        .setStyle(notificationStyle)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(DEFAULT_ALL)
        .build()
}