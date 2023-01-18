package com.godzuche.achivitapp.feature_task.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.AlarmManagerCompat
import com.godzuche.achivitapp.feature_task.data.receivers.Constants
import com.godzuche.achivitapp.feature_task.data.receivers.DueTaskAlarmReceiver
import com.godzuche.achivitapp.feature_task.domain.DueTaskAlarmScheduler
import com.godzuche.achivitapp.feature_task.domain.model.Task

class DueTaskAndroidAlarmScheduler(
    private val context: Context
) : DueTaskAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
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
        /*AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            taskDueDate,
            alarmPendingIntent
        )*/
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