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
import com.godzuche.achivitapp.data.local.database.model.asNetworkModel
import com.godzuche.achivitapp.data.repository.CATEGORY_PATH
import com.godzuche.achivitapp.data.repository.COLLECTION_PATH
import com.godzuche.achivitapp.data.repository.TASKS_PATH
import com.godzuche.achivitapp.data.repository.USERS_PATH
import com.godzuche.achivitapp.domain.model.asEntity
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

@HiltWorker
class FirebaseWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val firestoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Timber.d("FirebaseWorker doWork() called")

        return withContext(ioDispatcher) {

            val taskId = inputData.getInt(TASK_ID, -1)
            val workName = inputData.getString(WORK_NAME) ?: ""

            val isWorkSuccessful = suspendRunCatching {

                val task = async(ioDispatcher) { taskRepository.retrieveTask(taskId) }.await()

                firebaseAuth.currentUser?.let {
                    val taskDocRef = firestoreDb.collection(USERS_PATH).document(it.uid)
                        .collection(CATEGORY_PATH)
                        .document(task.categoryTitle)
                        .collection(COLLECTION_PATH)
                        .document(task.collectionTitle)
                        .collection(TASKS_PATH)
                        .document(task.id.toString())

                    when (workName) {
                        FirebaseWorkHelper.FirebaseWorkerName.ADD.name -> {
                            taskDocRef.set(task.asEntity().asNetworkModel()).await()
                        }

                        FirebaseWorkHelper.FirebaseWorkerName.UPDATE.name -> {
                            taskDocRef.set(task.asEntity().asNetworkModel(), SetOptions.merge())
                                .await()
                        }

                        else -> Unit
                    }

                }

            }.isSuccess

            if (isWorkSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        }
    }

    companion object {
        fun buildAddTaskWork(taskId: Int, firebaseWorkerName: String) =
            OneTimeWorkRequestBuilder<FirebaseWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(FirebaseWorkConstraints)
                .setInputData(
                    workDataOf(
                        TASK_ID to taskId,
                        WORK_NAME to firebaseWorkerName
                    )
                )
                .build()
    }

}