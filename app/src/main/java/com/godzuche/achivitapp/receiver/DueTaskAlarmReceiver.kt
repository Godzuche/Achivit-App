package com.godzuche.achivitapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.godzuche.achivitapp.worker.DueTaskWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DueTaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val messageBundle = intent?.extras ?: return

        val taskId = messageBundle.getInt(Constants.KEY_TASK_ID)
        // A unique name for each task so that each task can have a separate worker
        val dueTaskWorkUniqueName = DUE_TASK_WORK_NAME + taskId

        // Run due task work and ensure only one worker per task runs at any time
        context?.let { WorkManager.getInstance(it) }?.apply {
            enqueueUniqueWork(
                dueTaskWorkUniqueName,
                ExistingWorkPolicy.REPLACE,
                DueTaskWorker.buildDueTaskWork(taskId)
            )
        }
    }

    companion object {
        private const val DUE_TASK_WORK_NAME = "DueTaskWorkName"
    }
}