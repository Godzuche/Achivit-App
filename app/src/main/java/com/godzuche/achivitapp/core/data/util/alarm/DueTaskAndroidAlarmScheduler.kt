package com.godzuche.achivitapp.core.data.util.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.core.common.util.Constants
import com.godzuche.achivitapp.feature.tasks.presentation.receiver.DueTaskAlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class DueTaskAndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : DueTaskAlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(task: Task) {
        Timber.tag(TAG).d("schedule() fun called")

        val extras = Bundle().apply {
            task.id?.let {
                putInt(Constants.KEY_TASK_ID, it)
            }
        }
        val intent = Intent(context, DueTaskAlarmReceiver::class.java).apply {
            putExtras(extras)
        }
        val pendingIntent = getAlarmPendingIntent(task, intent)

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
                    pendingIntent,
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
                    pendingIntent,
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                task.dueDate,
                pendingIntent,
            )
        }
    }

    override fun cancel(task: Task) {
        alarmManager.cancel(
            getAlarmPendingIntent(
                task = task,
                intent = Intent(context, DueTaskAlarmReceiver::class.java),
            )
        )
    }

    private fun getAlarmPendingIntent(
        task: Task,
        intent: Intent,
    ): PendingIntent {
        val requestCode = task.id.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return pendingIntent
    }
    
    companion object {
        private const val TAG = "DueTaskAndroidAlarmScheduler"
    }
}