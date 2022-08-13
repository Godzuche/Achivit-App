package com.godzuche.achivitapp.feature_task.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DueTaskWorker(
    ctx: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(ctx, workerParams) {
    override suspend fun doWork(): Result {
        // Do some background work/task
        return Result.success()
    }
}