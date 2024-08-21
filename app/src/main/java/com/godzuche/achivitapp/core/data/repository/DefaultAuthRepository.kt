package com.godzuche.achivitapp.core.data.repository

import android.net.Uri
import com.godzuche.achivitapp.BuildConfig
import com.godzuche.achivitapp.core.common.AchivitDispatchers
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.common.Dispatcher
import com.godzuche.achivitapp.core.common.util.Constants.CATEGORY_PATH
import com.godzuche.achivitapp.core.common.util.Constants.COLLECTION_PATH
import com.godzuche.achivitapp.core.common.util.Constants.TASKS_PATH
import com.godzuche.achivitapp.core.common.util.Constants.USERS_PATH
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCategoryDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskCollectionDao
import com.godzuche.achivitapp.core.data.local.database.dao.TaskDao
import com.godzuche.achivitapp.core.data.local.database.model.asNetworkModel
import com.godzuche.achivitapp.core.data.remote.model.NetworkTask
import com.godzuche.achivitapp.core.data.remote.model.NetworkTaskCategory
import com.godzuche.achivitapp.core.data.remote.model.NetworkTaskCollection
import com.godzuche.achivitapp.core.data.remote.model.NetworkUserData
import com.godzuche.achivitapp.core.data.remote.model.asEntity
import com.godzuche.achivitapp.core.data.remote.model.asExternalModel
import com.godzuche.achivitapp.core.domain.model.UserData
import com.godzuche.achivitapp.core.domain.model.asNewNetworkUserData
import com.godzuche.achivitapp.core.domain.repository.AuthRepository
import com.godzuche.achivitapp.core.domain.util.NetworkMonitor
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class DefaultAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val oneTapClient: SignInClient,
    private val firestoreDb: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val categoryDao: TaskCategoryDao,
    private val collectionDao: TaskCollectionDao,
    private val taskDao: TaskDao,
    @Dispatcher(AchivitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    private val _isNewUser = MutableStateFlow(false)
    private val isNewUser = _isNewUser.asStateFlow()

    override fun requestOneTapSignIn(): Flow<AchivitResult<BeginSignInResult>> =
        flow {
            // Try launching the One Tap sign-in flow
            try {
                emit(AchivitResult.Loading)
                val beginSignInResult = oneTapClient.beginSignIn(buildSignInRequest()).await()
                emit(AchivitResult.Success(data = beginSignInResult))
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(AchivitResult.Error(exception = e))

                // No saved credentials found. Try launching the One Tap sign-up flow
                try {
                    emit(AchivitResult.Loading)
                    val beginSignUpResult = oneTapClient.beginSignIn(buildSignUpRequest()).await()
                    // New user because this is sign-up flow
                    emit(AchivitResult.Success(data = beginSignUpResult))
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e is CancellationException) throw e
                    emit(AchivitResult.Error(exception = e))
                }
            }
        }

    override fun googleSignInWithCredential(credential: SignInCredential): Flow<AchivitResult<UserData?>> =
        flow {
            emit(AchivitResult.Loading)
            try {
                val googleIdToken = credential.googleIdToken
                /*val username = credential.id
                val password = credential.password*/

                val authResult = googleIdToken?.let { idToken ->
                    val googleAuthCredentials = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(googleAuthCredentials).await()
                }
                val firebaseUser = authResult?.user

                _isNewUser.update { authResult?.additionalUserInfo?.isNewUser == true }

                // Add user to Cloud Firestore db only after completed sign up
                if (isNewUser.value) {
                    addUserToFirestore()
                }


                val userDocRef = firebaseUser?.let {
                    firestoreDb.collection(USERS_PATH).document(firebaseUser.uid)
                }

                syncUserData(userDocRef)

                val firestoreUser =
                    userDocRef?.get()?.await()?.toObject(NetworkUserData::class.java)

                Timber.tag("DefaultAuthRepository")
                    .d("Firestore user creationDate: ${firestoreUser?.createdDate?.toDate()?.time?.millisToString()}")

                emit(AchivitResult.Success(data = firestoreUser?.asExternalModel()))
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(AchivitResult.Error(exception = e))
            }
        }
            .flowOn(ioDispatcher)

    private suspend fun syncUserData(userDocRef: DocumentReference?) {
        if (isNewUser.value) {
            Timber.d("Auth: New User")

            withContext(ioDispatcher) {
                categoryDao.retrieveCategoryWithCollectionsAndTasks().forEach {
                    userDocRef?.collection(CATEGORY_PATH)
                        ?.document(it.category.title)
                        ?.set(it.category.asNetworkModel())
                        ?.await()

                    it.collectionWithTasks.map { it.collection }.forEach { collection ->
                        userDocRef
                            ?.collection(CATEGORY_PATH)
                            ?.document(collection.categoryTitle)
                            ?.collection(COLLECTION_PATH)
                            ?.document(collection.title)
                            ?.set(collection.asNetworkModel())
                            ?.await()
                    }

                    it.collectionWithTasks.flatMap { it.tasks }.forEach { taskEntity ->
                        userDocRef
                            ?.collection(CATEGORY_PATH)
                            ?.document(taskEntity.categoryTitle)
                            ?.collection(COLLECTION_PATH)
                            ?.document(taskEntity.collectionTitle)
                            ?.collection(TASKS_PATH)
                            ?.document(taskEntity.id.toString())
                            ?.set(taskEntity.asNetworkModel())
                            ?.await()
                    }
                }
            }
        } else {
            Timber.d("Auth: Old User")

            val categories = userDocRef
                ?.collection(CATEGORY_PATH)
                ?.get()?.await()
                ?.toObjects(NetworkTaskCategory::class.java)
                ?.toList() ?: emptyList()

            val collections = firestoreDb
                .collectionGroup(COLLECTION_PATH)
                .get().await().toObjects(NetworkTaskCollection::class.java)
                .toList()

            val tasks = firestoreDb
                .collectionGroup(TASKS_PATH)
                .get().await().toObjects(NetworkTask::class.java)
                .toList()

            Timber.tag("Auth").d("categories: %s", categories)
            Timber.tag("Auth").d("collections: %s", collections)
            Timber.tag("Auth").d("tasks: %s", tasks)

            upsertCategoryWithCollectionsAndTasks(
                categories,
                collections,
                tasks,
            )

        }
    }

    private suspend fun upsertCategoryWithCollectionsAndTasks(
        categories: List<NetworkTaskCategory>,
        collections: List<NetworkTaskCollection>,
        tasks: List<NetworkTask>,
    ) {
        withContext(ioDispatcher) {
            try {
                Timber.tag("TaskRepository").d("Starting upsert operation")

                // Verify data integrity
                val isValid = verifyDataIntegrity(categories, collections, tasks)
                if (!isValid) {
                    Timber.tag("TaskRepository").e("Data integrity check failed")
                    return@withContext
                }

                // Upsert categories first
                val categoryEntities = categories.map(NetworkTaskCategory::asEntity)
                Timber.tag("TaskRepository").d("Upserting categories: %s", categoryEntities)
                categoryDao.upsertCategories(categoryEntities)

                // Upsert collections next
                val collectionEntities = collections.map(NetworkTaskCollection::asEntity)
                Timber.tag("TaskRepository").d("Upserting collections: %s", collectionEntities)
                collectionDao.upsertCollections(collectionEntities)

                // Upsert tasks last
                val taskEntities = tasks.map(NetworkTask::asEntity)
                Timber.tag("TaskRepository").d("Upserting tasks: %s", taskEntities)
                taskDao.upsertTasks(taskEntities)

                Timber.tag("TaskRepository").d("Upsert operation completed successfully")
            } catch (e: Exception) {
                Timber.tag("TaskRepository").e(e, "Error during upsert operation")
                throw e
            }
        }
    }

    private fun verifyDataIntegrity(
        categories: List<NetworkTaskCategory>,
        collections: List<NetworkTaskCollection>,
        tasks: List<NetworkTask>
    ): Boolean {
        val categoryIds = categories.map { it.title }.toSet()
        Timber.tag("TaskRepository").d("cat ids: %s", categoryIds)
        val collectionIds = collections.map { it.title }.toSet()
        Timber.tag("TaskRepository").d("coll ids: %s", collectionIds)
        val collectionCategoryIds = collections.map { it.categoryTitle }.toSet()
        Timber.tag("TaskRepository").d("collCat ids: %s", collectionCategoryIds)
        val taskCollectionIds = tasks.map { it.collectionTitle }.toSet()
        Timber.tag("TaskRepository").d("taskColl ids: %s", taskCollectionIds)

        val validCollections = collectionCategoryIds.all { it in categoryIds }
        Timber.tag("TaskRepository").d("isValidColls: %s", validCollections)
        val validTasks = taskCollectionIds.all { it in collectionIds }
        Timber.tag("TaskRepository").d("isValidTasks: %s", validTasks)

        return validCollections && validTasks
    }

    private suspend fun addUserToFirestore() {
        auth.currentUser?.let { firebaseUser ->

            val userData = firebaseUser.asNewNetworkUserData()

            firestoreDb.collection(USERS_PATH)
                .document(userData.userId)
                .set(userData)
                .await()
        }
    }

    override fun signOut(): Flow<AchivitResult<Nothing?>> = flow {
        try {
            emit(AchivitResult.Loading)
            oneTapClient.signOut().await()
//            auth.currentUser?.unlink(GoogleAuthProvider.PROVIDER_ID)
            auth.signOut()
            emit(AchivitResult.Success(data = null))
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }

    }

    override fun getSignedInUser(): Flow<AchivitResult<UserData?>> = flow {
        try {
            emit(AchivitResult.Loading)

            val source = if (networkMonitor.isOnline) Source.SERVER else Source.CACHE

            val signedInUser = auth.currentUser
            val isUserSignedIn = (signedInUser != null)
            val userDocRef = signedInUser?.let {
                firestoreDb.collection(USERS_PATH).document(it.uid)
            }
            val firestoreUser =
                userDocRef?.get(source)?.await()?.toObject(NetworkUserData::class.java)

            if (isUserSignedIn) {
                emit(
                    AchivitResult.Success(
                        data = firestoreUser?.asExternalModel()
                    )
                )
            } else {
                emit(AchivitResult.Success(data = null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }
    }

    override fun updateUserProfile(
        photoUri: Uri,
        displayName: String
    ): Flow<AchivitResult<Nothing?>> = flow {
        try {
            emit(AchivitResult.Loading)
            val profileUpdates = userProfileChangeRequest {
                this.photoUri = photoUri
                this.displayName = displayName
            }
            auth.currentUser!!.updateProfile(profileUpdates).await()
            emit(AchivitResult.Success(data = null))
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            emit(AchivitResult.Error(exception = e))
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest =
        BeginSignInRequest
            .builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(BuildConfig.CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

    private fun buildSignUpRequest(): BeginSignInRequest =
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.CLIENT_ID)
                    .build()
            )
            .build()

}