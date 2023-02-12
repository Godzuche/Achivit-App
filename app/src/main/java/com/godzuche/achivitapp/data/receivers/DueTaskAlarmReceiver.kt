package com.godzuche.achivitapp.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.godzuche.achivitapp.data.worker.DueTaskWorker
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class DueTaskAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.tag(TAG).d("Receiver called")

        val messageBundle = intent?.extras ?: return

        val taskId = messageBundle.getInt(Constants.KEY_TASK_ID)
        val dueTaskRequest = OneTimeWorkRequestBuilder<DueTaskWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "taskId" to taskId
                )
            )
            .addTag("due_task")
            .build()

        val workManager = context?.let { WorkManager.getInstance(it) }
        workManager?.enqueueUniqueWork(
            DueTaskWorker.TAG + taskId.toString(),
            ExistingWorkPolicy.REPLACE,
            dueTaskRequest
        )
    }

    companion object {
        private const val TAG = "DueTaskAlarmReceiver"
    }
}