package com.godzuche.achivitapp.feature_task.presentation.state_holder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.data.local.entity.Category
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategoryEntity
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.domain.use_case.GetTask
import com.godzuche.achivitapp.feature_task.presentation.TasksUiEvent
import com.godzuche.achivitapp.feature_task.presentation.ui_state.TasksUiState
import com.godzuche.achivitapp.feature_task.presentation.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTask: GetTask,
//    private val getAllTasks: GetTasks,
//    private val searchTasksByTitle: SearchTasksByTitle,
//    private val insertTask: InsertTask,
    private val repository: TaskRepository,
) : ViewModel() {

    val bottomSheetAction = MutableStateFlow<String>("")
    val bottomSheetTaskId = MutableStateFlow<Int>(-1)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    val tasksPagingDataFlow: Flow<PagingData<Task>>

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var deletedTask: Task? = null

    val categories = repository.getAllCategory()
        .map {
            it.map { category ->
                category.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val collections = repository.getAllCollection()
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

    fun addNewCategory(title: String) {
        viewModelScope.launch { repository.insertCategory(Category(title = title)) }
    }

    fun addNewCollection(title: String) {
        viewModelScope.launch { repository.insertCollection(TaskCollection(title = title)) }
    }

    val accept: (TasksUiEvent) -> Unit

    private var searchJob: Job? = null
    private fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            repository.searchTasksByTitle(query).onEach { result ->
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
        searchJob = viewModelScope.launch {
            repository.searchTasksByTitle(query).onEach { result ->
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

        tasksPagingDataFlow = repository.getAllTask().cachedIn(viewModelScope)

        accept = { action ->
            when (action) {
                is TasksUiEvent.OnDeleteFromTaskDetail -> {
                    deletedTask = action.deletedTask
                    viewModelScope.launch {
                        delay(500L)
                        sendUiEvent(UiEvent.ShowSnackBar(
                            message = "Task deleted",
                            action = SnackBarActions.UNDO
                        ))
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
                        repository.deleteTask(action.task)
                        sendUiEvent(UiEvent.ShowSnackBar(
                            "Task deleted!",
                            SnackBarActions.UNDO
                        ))
                    }
                }
                is TasksUiEvent.OnUndoDeleteClick -> {
                    deletedTask?.let { it ->
                        viewModelScope.launch {
                            repository.reInsertTask(it)
                        }
                    }
                }
                is TasksUiEvent.OnDoneChange -> {
                    viewModelScope.launch {
                        repository.updateTask(
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

//    val TasksUiState.shouldScrollToTop: Boolean get() =

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun addNewTask(
        taskTitle: String,
        taskDescription: String,
        dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ) {
        val newTask = getNewTaskEntry(taskTitle, taskDescription, dateSelection, sHour, mMinute)
        insertTask(newTask)
        viewModelScope.launch {
            delay(300L)
            sendUiEvent(UiEvent.ScrollToTop)
        }
    }

    private fun getNewTaskEntry(
        taskTitle: String,
        taskDescription: String,
        dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ): Task {
        return Task(
            title = taskTitle,
            description = taskDescription,
            /* completed = done*/
            date = dateSelection,
            hours = sHour,
            minutes = mMinute
        )
    }

    // Input title validation
    fun isEntryValid(taskTitle: String, chipCount: Int): Boolean {
        return taskTitle.isNotBlank() && chipCount > 0
    }

    fun retrieveTask(id: Long): Flow<Task> {
        return repository.getTask(id).mapLatest {
            it.data!!
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun undoDelete(task: Task) {
        insertTask(task)
    }

    fun updateTask(
        taskId: Long, taskTitle: String, taskDescription: String, dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ) {
        val updatedTask =
            getUpdatedTaskEntry(taskId, taskTitle, taskDescription, dateSelection, sHour, mMinute)
        updateTask(updatedTask)
    }

    private fun getUpdatedTaskEntry(
        taskId: Long,
        taskTitle: String,
        taskDescription: String,
        dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ): Task {
        return Task(
            id = taskId,
            title = taskTitle,
            description = taskDescription,
            date = dateSelection,
            hours = sHour,
            minutes = mMinute
        )
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun reorderTasks(draggedTask: Task, targetTask: Task) {
        updateTask(draggedTask.copy(id = targetTask.id))
        updateTask(targetTask.copy(id = draggedTask.id))
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