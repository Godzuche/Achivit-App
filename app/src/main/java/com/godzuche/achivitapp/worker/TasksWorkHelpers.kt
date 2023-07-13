package com.godzuche.achivitapp.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.task_detail.TaskDetailFragmentArgs
import com.godzuche.achivitapp.receiver.DUE_TASK_NOTIFICATION_CHANNEL_DESCRIPTION
import com.godzuche.achivitapp.receiver.DUE_TASK_NOTIFICATION_CHANNEL_ID
import com.godzuche.achivitapp.receiver.DUE_TASK_NOTIFICATION_CHANNEL_NAME

const val DAILY_NOTIFICATION_CHANNEL_NAME = "Daily Task Reminder"
const val DAILY_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notification reminder for tasks with high priority daily"
const val DAILY_NOTIFICATION_CHANNEL_ID = "DAILY_TASK_NOTIFICATION"
const val NOTIFICATION_TITLE = "Tasks Reminder"
private const val TASK_NOTIFICATION_GROUP = "TASK_NOTIFICATIONS"

fun Context.createDueTaskNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val id = DUE_TASK_NOTIFICATION_CHANNEL_ID
        val name = DUE_TASK_NOTIFICATION_CHANNEL_NAME
        val channelDescription = DUE_TASK_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance).apply {
            description = channelDescription
            enableVibration(true)
        }

        val notificationManager: NotificationManager? =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.createNotificationChannel(channel)
    }
}

fun Context.createDailyTaskNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val id = DAILY_NOTIFICATION_CHANNEL_ID
        val name = DAILY_NOTIFICATION_CHANNEL_NAME
        val channelDescription =
            DAILY_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        notificationManager?.createNotificationChannel(channel)
    }

}

fun Context.makeDueTaskNotification(
    taskId: Int,
    taskTitle: String,
    taskDescription: String,
) {
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val args = TaskDetailFragmentArgs(id = taskId).toBundle()

    val pendingIntent = NavDeepLinkBuilder(this)
        .setGraph(R.navigation.nav_graph_main)
        .setDestination(R.id.task_detail)
        .setArguments(args)
        .createPendingIntent()

    val notificationBuilder = NotificationCompat.Builder(
        this,
        DUE_TASK_NOTIFICATION_CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_baseline_check_box_24)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("\"${taskTitle}\" is due")
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(DEFAULT_ALL)
        .setColor(com.google.android.material.R.attr.colorPrimary)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setVibrate(LongArray(0))
        .setAutoCancel(true)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(taskDescription)
                .setBigContentTitle(taskTitle)
        )
        .setGroup(TASK_NOTIFICATION_GROUP)

    NotificationManagerCompat.from(this)
        .notify(taskId + 1, notificationBuilder.build())
}

fun makeDailyTaskNotification(context: Context, tasks: List<Task>) {
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

    val notificationBuilder = NotificationCompat.Builder(
        context,
        DAILY_NOTIFICATION_CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_baseline_check_box_24)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("You have ${tasks.size} tasks of high priority for today")
        .setContentIntent(pendingIntent)
        .setStyle(notificationStyle)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(DEFAULT_ALL)
        .build()
}