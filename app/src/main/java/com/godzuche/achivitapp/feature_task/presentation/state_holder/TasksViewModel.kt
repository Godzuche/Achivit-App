package com.godzuche.achivitapp.feature_task.presentation.state_holder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.util.Resource
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.domain.use_case.GetTask
import com.godzuche.achivitapp.feature_task.presentation.TasksUiEvent
import com.godzuche.achivitapp.feature_task.presentation.ui_state.TasksUiState
import com.godzuche.achivitapp.feature_task.presentation.util.Routes
import com.godzuche.achivitapp.feature_task.presentation.util.SnackBarActions
import com.godzuche.achivitapp.feature_task.presentation.util.UiEvent
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
//    private val getAllTasks: GetAllTasks,
//    private val searchTasksByTitle: SearchTasksByTitle,
//    private val insertTask: InsertTask,
    private val repositoryImpl: TaskRepository,
) : ViewModel() {

    val bottomSheetAction = MutableStateFlow<String>("")
    val bottomSheetTaskId = MutableStateFlow<Int>(-1)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var deletedTask: Task? = null

    val accept: (TasksUiEvent) -> Unit

    private var searchJob: Job? = null
    private fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            repositoryImpl.searchTasksByTitle(query).onEach { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data!!)
                    }
                }
            }.launchIn(this)
        }
    }

    private fun search(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            repositoryImpl.searchTasksByTitle(query).onEach { result ->
                if (result is Resource.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data!!)
                    }
                }
            }.launchIn(this)
        }
    }

    init {
        var noScrollPosition: Int = 0
        var lastScrollPosition: Int = 0

        viewModelScope.launch {
            repositoryImpl.getAllTask().collect { resource ->
                _uiState.update {
                    it.copy(tasksItems = resource.data!!)
                }
            }
        }

        accept = { action ->
            when (action) {
                is TasksUiEvent.Search -> {
                    search(action.query)
                }
                is TasksUiEvent.OnSearch -> {
                    onSearch(action.query)
                }
                is TasksUiEvent.OnScroll -> {
                    viewModelScope.launch {
                        lastScrollPosition = action.currentScrollPosition
                    }
                }
                is TasksUiEvent.OnAddTaskClick -> {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_TASK))
                }
                is TasksUiEvent.OnDeleteTask -> {
                    viewModelScope.launch {
                        deletedTask = action.task
                        repositoryImpl.deleteTask(action.task)
                        if (action.shouldPopBackStack) {
                            sendUiEvent(UiEvent.PopBackStack)
                        }
                        sendUiEvent(UiEvent.ShowSnackBar(
                            "Task deleted!",
                            SnackBarActions.UNDO
                        ))
                    }
                }
                is TasksUiEvent.OnUndoDeleteClick -> {
                    deletedTask?.let { it ->
                        viewModelScope.launch {
                            repositoryImpl.reInsertTask(it)
                        }
                    }
                }
                is TasksUiEvent.OnDoneChange -> {
                    viewModelScope.launch {
                        repositoryImpl.updateTask(
                            action.task.copy(
                                completed = action.isDone
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
            repositoryImpl.insertTask(task)
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

    fun retrieveTask(id: Int): Flow<Task> {
        return repositoryImpl.getTask(id).mapLatest {
            it.data!!
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repositoryImpl.deleteTask(task)
        }
    }

    fun undoDelete(task: Task) {
        insertTask(task)
    }

    fun updateTask(
        taskId: Int, taskTitle: String, taskDescription: String, dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ) {
        val updatedTask =
            getUpdatedTaskEntry(taskId, taskTitle, taskDescription, dateSelection, sHour, mMinute)
        updateTask(updatedTask)
    }

    private fun getUpdatedTaskEntry(
        taskId: Int,
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
            repositoryImpl.updateTask(task)
        }
    }

    fun reorderTasks(draggedTask: Task, targetTask: Task) {
        updateTask(draggedTask.copy(id = targetTask.id))
        updateTask(targetTask.copy(id = draggedTask.id))
    }

    fun onSearchClosed(): Boolean {
        viewModelScope.launch {
            repositoryImpl.getAllTask().collect { resource ->
                _uiState.update {
                    it.copy(tasksItems = resource.data!!)
                }
            }
        }
        return false
    }

}

/*class TaskViewModelFactory(
    private val getTask: GetTask,
    *//* private val getAllTasks: GetAllTasks,
     private val searchTasksByTitle: SearchTasksByTitle,
     private val insertTask: InsertTask,*//*
    private val repositoryImpl: TaskRepositoryImpl,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(
                getTask,
                *//*getAllTasks,
                searchTasksByTitle,
                insertTask,*//*
                repositoryImpl) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}*/