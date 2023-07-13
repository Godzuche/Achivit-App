package com.godzuche.achivitapp.feature.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.domain.repository.TaskCollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.domain.util.DueTaskAlarmScheduler
import com.godzuche.achivitapp.feature.tasks.ui_state.ModalBottomSheetUiState
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import com.godzuche.achivitapp.feature.tasks.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModalBottomSheetViewModel @Inject constructor(
    private val dueTaskAlarmScheduler: DueTaskAlarmScheduler,
    private val taskRepository: TaskRepository,
    private val taskCategoryRepository: TaskCategoryRepository,
    private val taskCollectionRepository: TaskCollectionRepository
) : ViewModel() {

    private val taskId = MutableStateFlow(-1)
    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    val categoryCollections: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    val uiStateFlow: StateFlow<ModalBottomSheetUiState>

    private val _uiEvent = MutableSharedFlow<UiEvent>()
//    val uiEvent = _uiEvent.asSharedFlow()

    val categories = taskCategoryRepository.getAllCategory()
        .map {
            it.map { category ->
                category.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val collections = taskCollectionRepository.getAllCollection()
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
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getTask(id)
                .onEach { result ->
                    when (result) {
                        is AchivitResult.Success -> {
                            _task.emit(result.data)
                        }

                        else -> {
                            _task.emit(null)
                        }
                    }
                }.launchIn(viewModelScope)
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
                bottomSheetActionTitle = action,
                task = task,
                taskId = taskId
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
        val status = if (dueDate > Clock.System.now().toEpochMilliseconds()) {
            TaskStatus.TODO
        } else {
            task.value?.status
        }

        return Task(
            id = taskId,
            title = taskTitle,
            description = taskDescription,
            created = task.value?.created!!,
            dueDate = dueDate,
            status = status!!,
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
            taskCollectionRepository.getCollection(collectionTitle).collectLatest {
                onGetCategoryTitle(it.categoryTitle)
            }
        }
    }

    /* fun getCategoryCollections(categoryTitle: String, onGetCollections: (List<String>) -> Unit) {
         viewModelScope.launch(Dispatchers.IO) {
             taskCategoryRepository.getCategoryWithCollectionsByTitle(categoryTitle)
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
            taskCategoryRepository.getCategoryWithCollectionsByTitle(categoryTitle)
                .collectLatest { listOfCategoryWithCollection ->
                    listOfCategoryWithCollection.map { categoryWithCollections ->
                        val collectionList = categoryWithCollections.collections.map { it.title }
                        categoryCollections.emit(collectionList)
                    }
                }
        }
    }

}