package com.godzuche.achivitapp.feature_task.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TASK_ID
import com.godzuche.achivitapp.feature_task.worker.DueTaskWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

@AndroidEntryPoint
class DueTaskAlarmReceiver() : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob())


    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.tag("Reminder").d("Receiver called")

//        val pendingResult: PendingResult = goAsync()

        val bundle = intent?.extras
        bundle?.apply {
            val taskId = getInt(KEY_TASK_ID)
            val dueTaskRequest = OneTimeWorkRequestBuilder<DueTaskWorker>()
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    workDataOf(
                        "taskId" to taskId
                    )
                )
                .addTag("due_task")
                .build()
            val workManager = context?.let { WorkManager.getInstance(it) }
           /* workManager.enqueueUniqueWork(
                DueTaskWorker.TAG,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                dueTaskRequest
            )*/
            workManager?.enqueue(dueTaskRequest)
            Timber.tag("Reminder")
                .d("Receiver received title: " + " with id: " + taskId)

        }
    }

    companion object {
        private const val TAG = "DueTaskAlarmReceiver"
    }
}