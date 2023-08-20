package com.godzuche.achivitapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.godzuche.achivitapp.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

const val TASK_ID = "TaskId"
const val WORK_NAME = "WorkName"

val FirebaseWorkConstraints = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .build()

class FirebaseWorkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun addTask(task: Task) {
        task.id?.let {
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    FirebaseWorkerName.ADD.name + task.id,
                    ExistingWorkPolicy.REPLACE,
                    FirebaseWorker.buildAddTaskWork(it, FirebaseWorkerName.ADD.name)
                )
            }
        }
    }

    fun updateTask(task: Task) {
        task.id?.let {
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    FirebaseWorkerName.UPDATE.name + task.id,
                    ExistingWorkPolicy.REPLACE,
                    FirebaseWorker.buildAddTaskWork(it, FirebaseWorkerName.UPDATE.name)
                )
            }
        }
    }

    enum class FirebaseWorkerName {
        DELETE,
        ADD,
        UPDATE,
    }

}