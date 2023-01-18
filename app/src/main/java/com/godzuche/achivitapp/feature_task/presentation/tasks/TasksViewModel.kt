package com.godzuche.achivitapp.feature_task.presentation.tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.CategoryRepository
import com.godzuche.achivitapp.feature_task.domain.repository.CollectionRepository
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.ui_state.TasksUiState
import com.godzuche.achivitapp.feature_task.presentation.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val app: Application,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : AndroidViewModel(app) {

    val dueTaskAlarmScheduler = DueTaskAndroidAlarmScheduler(app.applicationContext)

    val checkedChipId = MutableStateFlow<Int>(0)

    private var created = 0L

    val bottomSheetAction = MutableStateFlow("")
    val bottomSheetTaskId = MutableStateFlow(-1)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    val tasksPagingDataFlow: Flow<PagingData<Task>>
        get() = taskRepository.getAllTask().cachedIn(viewModelScope)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var deletedTask: Task? = null

    val categoryCollections: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

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

    private val _filter = MutableStateFlow(TaskFilter())
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    fun filter(
        category: TaskCategoryEntity,
        collection: TaskCollectionEntity,
        filterStatus: TaskStatus,
    ) {
        _filter.update {
            it.copy(
                category = category,
                collection = collection,
                status = filterStatus
            )
        }
    }

    fun setIsCompleted(task: Task, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.tag("CheckBox").i("ViewModel: $isChecked")
            taskRepository.updateTask(
                task.copy(
                    isCompleted = isChecked,
                    status = if (isChecked) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
                )
            )
        }
    }

    val accept: (TasksUiEvent) -> Unit

    private var searchJob: Job? = null
    private fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            taskRepository.searchTasksByTitle(query).onEach { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data!!)
                    }
                }
            }.launchIn(viewModelScope)
            delay(500L)
            _uiEvent.emit(UiEvent.ScrollToTop)
        }
    }

    private fun search(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            taskRepository.searchTasksByTitle(query).onEach { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data!!)
                    }
                }
            }.launchIn(viewModelScope)
            _uiEvent.emit(UiEvent.ScrollToTop)
        }
    }

    init {
        var noScrollPosition: Int = 0
        var lastScrollPosition: Int = 0

        /*   viewModelScope.launch {
               repository.getAllTask().collect { resource ->
                   _uiState.update {
                       it.copy(tasksItems = resource.data!!)
                   }
               }
           }*/

        accept = { action ->
            when (action) {
                is TasksUiEvent.OnDeleteFromTaskDetail -> {
                    deletedTask = action.deletedTask
                    viewModelScope.launch {
                        delay(400L)
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                message = "Task deleted",
                                action = SnackBarActions.UNDO
                            )
                        )
                    }
                }
                is TasksUiEvent.Search -> {
                    search(action.query)
                }
                is TasksUiEvent.OnSearch -> {
                    onSearch(action.query)
                }
                is TasksUiEvent.OnScroll -> {
                    viewModelScope.launch {
//                        lastScrollPosition = action.currentScrollPosition
                        /*_uiState.update {
                            it.copy(
                                lastScrolledPosition = action.currentScrollPosition
                            )
                        }*/
                    }
                }
                is TasksUiEvent.OnAddTaskClick -> {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_TASK))
                }
                is TasksUiEvent.OnDeleteTask -> {
                    deletedTask = action.task
                    viewModelScope.launch {
                        /*if (action.shouldPopBackStack) {
                            sendUiEvent(UiEvent.PopBackStack)
                        }*/
                        taskRepository.deleteTask(action.task)
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                "Task deleted!",
                                SnackBarActions.UNDO
                            )
                        )
                    }
                }
                is TasksUiEvent.OnUndoDeleteClick -> {
                    deletedTask?.let { it ->
                        viewModelScope.launch {
                            taskRepository.reInsertTask(it)
                        }
                    }
                }
                is TasksUiEvent.OnDoneChange -> {
                    viewModelScope.launch {
                        taskRepository.updateTask(
                            action.task.copy(
//                                completed = action.isDone
                            )
                        )
                    }
                }
                is TasksUiEvent.OnTaskClick -> {
                    sendUiEvent(UiEvent.Navigate(Routes.EDIT_TASK))
                }
                else -> Unit
            }
        }

    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.insertTask(task)
        }
    }

/*
    private suspend fun insertAndGetId(task: Task): Int {
        var id: Int? = null
        val deferred = viewModelScope.async(Dispatchers.IO) {
            repository.insertAndGetTask(task).collect()
        }
        return deferred.await()
    }
*/

    fun addNewTask(
        categoryTitle: String,
        collectionTitle: String,
        taskTitle: String,
        taskDescription: String,
        dueDate: Long,
    ) {

        created = Calendar.getInstance().timeInMillis
        /*val category = getCategoryEntry(categoryTitle)
        val collection = getCollectionEntry(collectionTitle, categoryTitle)*/
        val newTask = getNewTaskEntry(
            taskTitle,
            taskDescription,
            created = created,
            dueDate = dueDate,
            collectionTitle
        )

        viewModelScope.launch(Dispatchers.IO) {
            val insertedTaskId = taskRepository.insertAndGetTask(newTask)

            dueTaskAlarmScheduler.schedule(
                newTask.copy(id = insertedTaskId)
            )

            Timber.tag("Reminder").d("ViewModel Set at %s", dueDate.toString())
        }

        viewModelScope.launch {
            delay(300L)
            sendUiEvent(UiEvent.ScrollToTop)
        }
    }

    private fun getCollectionEntry(
        collectionTitle: String,
        categoryTitle: String,
    ): TaskCollectionEntity {
        return TaskCollectionEntity(
            title = collectionTitle,
            categoryTitle = categoryTitle
        )
    }

    private fun getNewTaskEntry(
        taskTitle: String,
        taskDescription: String,
        created: Long,
        dueDate: Long,
        collectionTitle: String,
    ): Task {
        return Task(
            title = taskTitle,
            description = taskDescription,
            created = created,
            dueDate = dueDate,
            collectionTitle = collectionTitle
        )
    }

    // Input title validation
    fun isEntryValid(taskTitle: String, chipCount: Int): Boolean {
        return taskTitle.isNotBlank() && chipCount > 0
    }

    fun retrieveTask(id: Int): Flow<Task> {
        return taskRepository.getTask(id).mapLatest {
            it.data!!
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun undoDelete(task: Task) {
        insertTask(task)
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun reorderTasks(draggedTask: Task, targetTask: Task) {
        updateTask(draggedTask.copy(id = targetTask.id))
        updateTask(targetTask.copy(id = draggedTask.id))
    }

    fun setCheckedCategoryChip(checkedId: Int) {
        viewModelScope.launch {
            checkedChipId.emit(checkedId)
        }
    }

/*    fun onSearchClosed(): Boolean {
        viewModelScope.launch {
            repository.getAllTask().collect { resource ->
                _uiState.update {
                    it.copy(tasksItems = resource.data!!)
                }
            }
        }
        viewModelScope.launch {
            delay(500L)
            _uiEvent.emit(UiEvent.ScrollToTop)
        }
        return false
    }*/

}