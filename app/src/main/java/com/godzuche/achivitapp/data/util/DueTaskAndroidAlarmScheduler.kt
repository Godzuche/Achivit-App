package com.godzuche.achivitapp.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.receiver.Constants
import com.godzuche.achivitapp.receiver.DueTaskAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class DueTaskAndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : DueTaskAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
        Timber.tag("Add Task").d("schedule() fun called in DueTaskAndroidAlarmScheduler")

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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                /*alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )*/
                val alarmClockInfo = AlarmManager.AlarmClockInfo(task.dueDate, pendingIntent)
                alarmManager.setAlarmClock(
                    alarmClockInfo,
                    pendingIntent
                )
            } else {
                //
                /*// Ask users to go to exact alarm page in system settings.
                startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))*/
                // Permission not yet approved. Display user notice and revert to a fallback
                // approach.
//                alarmManager.setWindow()
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    task.dueDate,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                task.dueDate,
                pendingIntent
            )
        }
    }

    override fun cancel(task: Task) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        )
    }
}