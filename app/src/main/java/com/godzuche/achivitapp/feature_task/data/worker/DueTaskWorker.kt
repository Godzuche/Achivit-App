package com.godzuche.achivitapp.feature_task.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.godzuche.achivitapp.feature_notification.makeDueTaskNotification
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class DueTaskWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: TaskRepository,
) : CoroutineWorker(ctx, workerParams) {

    override suspend fun doWork(): Result {
        Timber.tag("Reminder").d("doWork() was called")
        val taskId = inputData.getInt("taskId", -1)
        val workDeferred = CoroutineScope(Dispatchers.Default).async {
            val task = CoroutineScope(Dispatchers.IO).async {
                Timber.d("Reminder", "getTaskOnce() called in worker")
                repo.getTaskOnce(taskId)
            }
            applicationContext.makeDueTaskNotification(
                taskId,
                task.await().title,
                task.await().description
            )
            withContext(Dispatchers.IO) {
                Timber.tag("Reminder").d("updateTaskStatus() called in worker")
                repo.updateTask(task.await().copy(status = TaskStatus.IN_PROGRESS))
            }
        }
        workDeferred.await()
        return Result.success()
    }

/*    private suspend fun Context.startForegroundService(
        taskId: Int,
        taskTitle: String,
        taskDescription: String,
    ) {
        Timber.d("Reminder", "startForegroundService() called in worker to show notification")

        val args = TaskDetailFragmentArgs(id = taskId).toBundle()

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph_main)
            .setDestination(R.id.task_fragment)
            .setArguments(args)
            .createPendingIntent()
        setForeground(
            ForegroundInfo(
                Constants.NOTIFICATION_ID,
                NotificationCompat.Builder(
                    this,
                    Constants.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_baseline_check_box_24)
                    .setContentTitle(Constants.NOTIFICATION_TITLE)
                    .setContentText("\"${taskTitle}\" is active now")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setColor(com.google.android.material.R.attr.colorPrimary)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setVibrate(LongArray(0))

                    .setAutoCancel(true)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(taskDescription)
                            .setBigContentTitle(taskTitle)
                    )
                    .build()
            )
        )
    }*/

    companion object {
        const val TAG = "DueTaskWorker"
    }
}