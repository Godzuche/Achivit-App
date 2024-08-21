package com.godzuche.achivitapp.feature.tasks.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.common.util.Constants.KEY_TASK_ID
import com.godzuche.achivitapp.core.domain.model.TaskStatus
import com.godzuche.achivitapp.core.domain.usecase.PersistNotificationUseCase
import com.godzuche.achivitapp.core.domain.usecase.RetrieveTaskUseCase
import com.godzuche.achivitapp.core.domain.usecase.UpdateTaskUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@HiltWorker
class DueTaskWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(AchivitDispatchers.DEFAULT) private val defaultDispatcher: CoroutineDispatcher,
    private val retrieveTaskUseCase: RetrieveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val persistNotificationUseCase: PersistNotificationUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(defaultDispatcher) {
            val taskId = inputData.getInt(KEY_TASK_ID, -1)

            val isSuccessful = suspendRunCatching {
                val task = async { retrieveTaskUseCase(taskId) }

                task.await()?.let {
                    updateTaskUseCase(it.copy(status = TaskStatus.IN_PROGRESS))

                    appContext.makeDueTaskNotification(
                        taskId = taskId,
                        taskTitle = it.title,
                        taskDescription = it.description,
                    )

                    persistNotificationUseCase(it)
                }
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
            .setInputData(workDataOf(KEY_TASK_ID to taskId))
            .addTag("due_task")
            .build()
    }
}