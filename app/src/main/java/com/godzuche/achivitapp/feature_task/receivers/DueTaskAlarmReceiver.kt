package com.godzuche.achivitapp.feature_task.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_DESC
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TASK_ID
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@AndroidEntryPoint
class DueTaskAlarmReceiver() : BroadcastReceiver() {
    private val scope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var repo: TaskRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Reminder", "Receiver called")

//        val pendingResult: PendingResult = goAsync()

        val bundle = intent?.extras
        bundle?.apply {
            val taskId = getInt(KEY_TASK_ID)
            val taskTitle = getString(KEY_TITLE)
            val taskDesc = getString(KEY_DESC)
            /*CoroutineScope(Dispatchers.IO).launch {
                repo.getTask(taskId).collectLatest {
                    val task = it.data
                    Log.d("Reminder",
                        "DI repo getTaskById title: ${task?.title} and id: ${task?.id}")
                }
            }*/

            if (taskTitle != null && taskDesc != null) {
                makeTaskDueNotification(
                    context!!,
                    taskId = taskId,
                    taskTitle = taskTitle,
                    taskDesc
                )
                /*makeTaskDueNotification(context!!, taskId = taskId, taskTitle = taskTitle, taskDesc)
                CoroutineScope(Dispatchers.IO).launch {
                    *//*repo.getTask(taskId).collect {
                        it.data?.let { task ->
                            repo.updateTask(task.copy(status = TaskStatus.IN_PROGRESS))
                        }
                    }*//*
                    repo.updateTaskStatus(taskId = taskId, status = TaskStatus.IN_PROGRESS)
                }*/
                /*scope.launch {
                    try {
                        makeTaskDueNotification(
                            context!!,
                            taskId = taskId,
                            taskTitle = taskTitle,
                            taskDesc
                        )
                        repo.updateTaskStatus(taskId = taskId, status = TaskStatus.IN_PROGRESS)
                    } finally {
                        pendingResult.finish()
                    }
                }*/
            }

            Log.d(
                "Reminder",
                "Receiver received title: $taskTitle with id: $taskId, description: $taskDesc"
            )

        }
    }

    companion object {
        private const val TAG = "DueTaskAlarmReceiver"
    }
}