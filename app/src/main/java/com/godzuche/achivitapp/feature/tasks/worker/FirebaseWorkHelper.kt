package com.godzuche.achivitapp.feature.tasks.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.godzuche.achivitapp.core.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class FirebaseWorkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun addTask(task: Task) {
        Timber.tag("Add Task").d("addTask() fun called in FirebaseWorkHelper")
        task.id?.let {
            Timber.tag("Add Task").d("in addTask() id not null")
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    FirebaseWorkerName.ADD.name + task.id,
                    ExistingWorkPolicy.REPLACE,
                    FirebaseWorker.buildFirebaseWork(taskId = it, FirebaseWorkerName.ADD.name)
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
                    FirebaseWorker.buildFirebaseWork(taskId = it, FirebaseWorkerName.UPDATE.name)
                )
            }
        }
    }

    fun deleteTask(task: Task) {
        task.id?.let {
            WorkManager.getInstance(context).apply {
                enqueueUniqueWork(
                    FirebaseWorkerName.DELETE.name + task.id,
                    ExistingWorkPolicy.REPLACE,
                    FirebaseWorker.buildFirebaseWork(taskId = it, FirebaseWorkerName.DELETE.name)
                )
            }
        }
    }

    enum class FirebaseWorkerName {
        DELETE,
        ADD,
        UPDATE,
    }

    companion object {
        const val TASK_ID = "TaskId"
        const val WORK_NAME = "WorkName"

        val FirebaseWorkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    }

}