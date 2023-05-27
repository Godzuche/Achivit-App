package com.godzuche.achivitapp.feature.notification

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
import com.godzuche.achivitapp.data.receivers.DUE_TASK_NOTIFICATION_CHANNEL_DESCRIPTION
import com.godzuche.achivitapp.data.receivers.DUE_TASK_NOTIFICATION_CHANNEL_ID
import com.godzuche.achivitapp.data.receivers.DUE_TASK_NOTIFICATION_CHANNEL_NAME
import com.godzuche.achivitapp.data.worker.Constants
import com.godzuche.achivitapp.data.worker.Constants.NOTIFICATION_TITLE
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature.feed.task_detail.TaskDetailFragmentArgs

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

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun Context.createDailyTaskNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val id = Constants.DAILY_NOTIFICATION_CHANNEL_ID
        val name = Constants.DAILY_NOTIFICATION_CHANNEL_NAME
        val channelDescription =
            Constants.DAILY_NOTIFICATION_CHANNEL_DESCRIPTION
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
    val args = TaskDetailFragmentArgs(id = taskId).toBundle()

    val pendingIntent = NavDeepLinkBuilder(this)
        .setGraph(R.navigation.nav_graph_main)
        .setDestination(R.id.task_detail)
        .setArguments(args)
        .createPendingIntent()

    val notificationBuilder = NotificationCompat.Builder(this, DUE_TASK_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_baseline_check_box_24)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText("\"${taskTitle}\" is active now")
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

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    // Using a constant notification id to have just one notification at a time.
    // Todo: Use the task id + 1 as the notification id
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
        Constants.DAILY_NOTIFICATION_CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_baseline_check_box_24)
        .setContentTitle(Constants.NOTIFICATION_TITLE)
        .setContentText("You have ${tasks.size} tasks of high priority for today")
        .setContentIntent(pendingIntent)
        .setStyle(notificationStyle)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(DEFAULT_ALL)
        .build()
}