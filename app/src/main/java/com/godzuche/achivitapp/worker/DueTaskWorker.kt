package com.godzuche.achivitapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.domain.model.toNotification
import com.godzuche.achivitapp.domain.repository.NotificationRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import timber.log.Timber

@HiltWorker
class DueTaskWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val taskRepository: TaskRepository,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(ioDispatcher) {

            val taskId = inputData.getInt("taskId", -1)

            val isSuccessful = suspendRunCatching {
                val task = async {
                    taskRepository.retrieveTask(taskId)
                }

                taskRepository.updateTask(
                    task.await().copy(
                        status = TaskStatus.IN_PROGRESS
                    )
                )

                appContext.makeDueTaskNotification(
                    taskId = taskId,
                    taskTitle = task.await().title,
                    taskDescription = task.await().description
                )

                notificationRepository.insertOrReplaceNotification(
                    notification = task.await()
                        .toNotification()
                        .copy(
                            isRead = false,
                            date = Clock.System.now()
                        )
                )
            }.isSuccess

            if (isSuccessful) Result.success() else Result.retry()
        }
    }

    companion object {
        /**
         * Expedited one time work to notify user of due tasks.
         * */
        fun buildDueTaskWork(taskId: Int) = OneTimeWorkRequestBuilder<DueTaskWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "taskId" to taskId
                )
            )
            .addTag("due_task")
            .build()
    }
}

/**
 * Attempts [block], returning a successful [Result] if it succeeds, otherwise a [Result.Failure]
 * taking care not to break structured concurrency
 */
private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Timber.tag("suspendRunCatching")
        .i(exception, "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result")
    Result.failure(exception)
}