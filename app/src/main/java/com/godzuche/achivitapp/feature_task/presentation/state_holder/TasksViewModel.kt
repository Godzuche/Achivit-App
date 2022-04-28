package com.godzuche.achivitapp.feature_task.presentation.state_holder

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import com.godzuche.achivitapp.feature_task.receivers.DueTaskAlarmReceiver
import com.godzuche.achivitapp.feature_task.receivers.setReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val app: Application,
    private val getTask: GetTask,
//    private val getAllTasks: GetTasks,
//    private val searchTasksByTitle: SearchTasksByTitle,
//    private val insertTask: InsertTask,
    private val repository: TaskRepository,
) : AndroidViewModel(app) {

    private val _checkedCategoryChip: MutableStateFlow<TaskCategoryEntity> = MutableStateFlow(
        TaskCategoryEntity(
            categoryId = 0L,
            title = "My Tasks"
        ))
    val checkedCategoryChip = _checkedCategoryChip.asStateFlow()

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

    val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmIntent = Intent(app, DueTaskAlarmReceiver::class.java)
    val alarmPendingIntent = PendingIntent.getBroadcast(
        getApplication(),
        0,
        alarmIntent,
        0
    )
    val notificationManager =
        app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    fun addNewTask(
        taskTitle: String,
        taskDescription: String,
        dateSelection: Long,
        sHour: Int,
        mMinute: Int,
    ) {
        val cal = Calendar.getInstance()
        cal.time = Date(dateSelection)
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MINUTE, mMinute)
            set(Calendar.HOUR_OF_DAY, sHour)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val newTask = getNewTaskEntry(taskTitle, taskDescription, dateSelection, sHour, mMinute)
        insertTask(newTask)
/*        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmPendingIntent
        ).also {
            Log.d("Reminder", "ViewModel Set at $sHour : $mMinute")
        }*/
        viewModelScope.launch {
            delay(300L)
            repository.getLastInsertedTask().collectLatest { task ->
                task.id?.let {
                    setReminder(app,
                        getApplication(),
                        it,
                        calendar.timeInMillis,
                        task.title,
                        task.description)
                    Log.d("Reminder", "ViewModel Set at $sHour : $mMinute")
                }
            }
        }
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

    fun setCheckedCategoryChip(checkedId: Int) {
        viewModelScope.launch {
            repository.getCategoryEntity(checkedId.toLong()).collectLatest { category ->
                Log.d("Category", "ViewModel collected checked chip: ${category.title}")
                _checkedCategoryChip.emit(category)
            }
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