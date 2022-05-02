package com.godzuche.achivitapp.feature_task.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_DESC
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TASK_ID
import com.godzuche.achivitapp.feature_task.receivers.Constants.KEY_TITLE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DueTaskAlarmReceiver() : BroadcastReceiver() {
    //    private var task: Task? = null
/*    @Inject
    lateinit var repo: TaskRepository*/

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Reminder", "Receiver called")

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
                makeTaskDueNotification(context!!, taskId = taskId, taskTitle = taskTitle, taskDesc)
            }

            Log.d("Reminder",
                "Receiver received title: $taskTitle with id: $taskId, description: $taskDesc")
//                    Log.d("Reminder", "Receiver received at due: ${it.hours} : ${it.minutes}")


        }
    }

    /*CoroutineScope(Dispatchers.IO).launch {
        if (taskId != null) {
            repository.getTask(taskId).collect {
                task = it.data!!

                // Todo: Create a dao function to toggle task complete status

            }
        }
    }*/
    /*  task?.let {
          if (context != null) {
              makeTaskDueNotification(context, it)
              Log.d("Reminder", "Receiver received at due: ${it.hours} : ${it.minutes}")

          }
      }*/
//    }
}