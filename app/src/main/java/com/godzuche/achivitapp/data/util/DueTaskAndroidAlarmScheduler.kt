package com.godzuche.achivitapp.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.receivers.Constants
import com.godzuche.achivitapp.receivers.DueTaskAlarmReceiver
import timber.log.Timber

class DueTaskAndroidAlarmScheduler(
    private val context: Context
) : DueTaskAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
        Timber.tag("DueTask").d("schedule function called")
        val extras = Bundle().apply {
            task.id?.let {
                putInt(Constants.KEY_TASK_ID, it)
            }
        }
        val intent = Intent(context, DueTaskAlarmReceiver::class.java).apply {
            putExtras(extras)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            task.dueDate,
            pendingIntent
        )
    }

    override fun cancel(task: Task) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}