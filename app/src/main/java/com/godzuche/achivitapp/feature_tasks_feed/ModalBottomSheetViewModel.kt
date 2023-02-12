package com.godzuche.achivitapp.feature_tasks_feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.data.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.CategoryRepository
import com.godzuche.achivitapp.domain.repository.CollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_tasks_feed.ui_state.ModalBottomSheetUiState
import com.godzuche.achivitapp.feature_tasks_feed.util.TaskStatus
import com.godzuche.achivitapp.feature_tasks_feed.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModalBottomSheetViewModel @Inject constructor(
    private val app: Application,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : AndroidViewModel(app) {

    private val dueTaskAlarmScheduler =
        DueTaskAndroidAlarmScheduler(
            context = app.applicationContext
        )

    private val taskId = MutableStateFlow(-1)
    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    val uiStateFlow: StateFlow<ModalBottomSheetUiState>

    val categoryCollections: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    private val _uiEvent = MutableSharedFlow<UiEvent>()
//    val uiEvent = _uiEvent.asSharedFlow()

    val categories = categoryRepository.getAllCategory()
        .map {
            it.map { category ->
                category.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val collections = collectionRepository.getAllCollection()
        .map {
            it.map { collection ->
                collection.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )


    private val collection: TaskCollectionEntity? = null
    private val filterStatus = TaskStatus.NONE

    val accept: (ModalBottomSheetUiEvent) -> Unit

    private val bottomSheetAction = taskId.transformLatest { taskId ->
        if (taskId == -1) {
            _task.emit(null)
            emit("Add Task")
        } else {
            retrieveTask(taskId)
            emit("Edit Task")
        }
    }/*.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")*/

    private fun retrieveTask(id: Int) {
        viewModelScope.launch {
            taskRepository.getTask(id)
                .map { it.data }
                .distinctUntilChanged()
                .collectLatest { task ->
                    _task.emit(task!!)
                }
        }
    }

    fun isEntryValid(
        category: String,
        collection: String,
        taskTitle: String,
        chipCount: Int,
    ): Boolean {
        return category.isNotBlank() && collection.isNotBlank() && taskTitle.isNotBlank() && chipCount > 0
    }

    init {

        uiStateFlow = combine(
            bottomSheetAction,
            _task,
            taskId
        ) { action, task, taskId ->
            ModalBottomSheetUiState(
                bottomSheetAction = action,
                task = task,
                id = taskId
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ModalBottomSheetUiState())

        accept = { action ->
            when (action) {
                is ModalBottomSheetUiEvent.OnGetBottomSheetAction -> {
                    viewModelScope.launch { taskId.emit(action.taskId) }
                }
            }
        }
    }

    fun updateTask(
        taskId: Int,
        taskTitle: String,
        taskDescription: String,
        dueDate: Long,
        collectionTitle: String,
        shouldReschedule: Boolean,
        categoryTitle: String
    ) {
        val updatedTask =
            getUpdatedTaskEntry(
                taskId,
                taskTitle,
                taskDescription,
                dueDate,
                collectionTitle,
                categoryTitle
            )
        updateTask(task = updatedTask, shouldReschedule = shouldReschedule)
    }

    private fun getUpdatedTaskEntry(
        taskId: Int,
        taskTitle: String,
        taskDescription: String,
        dueDate: Long,
        collectionTitle: String,
        categoryTitle: String,
    ): Task {
        return Task(
            id = taskId,
            title = taskTitle,
            description = taskDescription,
            created = task.value?.created!!,
            dueDate = dueDate,
            status = task.value?.status!!,
            collectionTitle = collectionTitle,
            categoryTitle = categoryTitle
        )
    }

    private fun updateTask(task: Task, shouldReschedule: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task)

            if (shouldReschedule) {
                dueTaskAlarmScheduler.schedule(task)
            }
        }
    }

    fun getCollectionCategory(collectionTitle: String, onGetCategoryTitle: ((String) -> Unit)) {
        viewModelScope.launch {
            collectionRepository.getCollection(collectionTitle).collectLatest {
                onGetCategoryTitle(it.categoryTitle)
            }
        }
    }

    /* fun getCategoryCollections(categoryTitle: String, onGetCollections: (List<String>) -> Unit) {
         viewModelScope.launch(Dispatchers.IO) {
             categoryRepository.getCategoryWithCollectionsByTitle(categoryTitle)
                 .collectLatest { listOfCategoryWithCollection ->
                     listOfCategoryWithCollection.map { categoryWithCollections ->
                         val collectionList = categoryWithCollections.collections.map { it.title }
                         categoryCollections.emit(collectionList)
                     }
                 }
         }
     }*/

    fun getCategoryCollections(categoryTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getCategoryWithCollectionsByTitle(categoryTitle)
                .collectLatest { listOfCategoryWithCollection ->
                    listOfCategoryWithCollection.map { categoryWithCollections ->
                        val collectionList = categoryWithCollections.collections.map { it.title }
                        categoryCollections.emit(collectionList)
                    }
                }
        }
    }

}