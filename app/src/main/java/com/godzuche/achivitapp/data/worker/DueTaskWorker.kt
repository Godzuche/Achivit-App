package com.godzuche.achivitapp.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature.feed.util.TaskStatus
import com.godzuche.achivitapp.feature.notification.makeDueTaskNotification
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
//        val e = measureTimeMillis{
        val taskId = inputData.getInt("taskId", -1)
        val workDeferred = CoroutineScope(Dispatchers.Default).async {
            val task = CoroutineScope(Dispatchers.IO).async {
                repo.getTaskOnce(taskId)
            }
            applicationContext.makeDueTaskNotification(
                taskId,
                task.await().title,
                task.await().description
            )
            withContext(Dispatchers.IO) {
                repo.updateTask(task.await().copy(status = TaskStatus.IN_PROGRESS))
            }
        }
        workDeferred.await()
//        }
//        println("doWork took: $e")
        return Result.success()
    }

    companion object {
        const val TAG = "DueTaskWorker"
    }
}