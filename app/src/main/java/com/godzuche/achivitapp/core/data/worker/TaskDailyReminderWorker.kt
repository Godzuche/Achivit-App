package com.godzuche.achivitapp.core.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class TaskDailyReminderWorker(
    context: Context, workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        // should get list of high priority tasks for the day by 12 am
        return Result.success()
    }
}