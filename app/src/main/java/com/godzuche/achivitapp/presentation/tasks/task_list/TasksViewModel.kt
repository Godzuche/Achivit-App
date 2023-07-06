package com.godzuche.achivitapp.presentation.tasks.task_list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.data.local.database.model.TaskCollectionEntity
import com.godzuche.achivitapp.data.util.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.domain.repository.TaskCollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.presentation.home.presentation.fromModifiedStatusText
import com.godzuche.achivitapp.presentation.tasks.ui_state.TasksUiState
import com.godzuche.achivitapp.presentation.tasks.util.Routes
import com.godzuche.achivitapp.presentation.tasks.util.SnackBarActions
import com.godzuche.achivitapp.presentation.tasks.util.TaskStatus
import com.godzuche.achivitapp.presentation.tasks.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val app: Application,
    private val taskRepository: TaskRepository,
    private val taskCategoryRepository: TaskCategoryRepository,
    private val taskCollectionRepository: TaskCollectionRepository
) : AndroidViewModel(app) {

    private val dueTaskAlarmScheduler =
        DueTaskAndroidAlarmScheduler(app.applicationContext)

    private var created = 0L

    val bottomSheetAction = MutableStateFlow("")
    val bottomSheetTaskId = MutableStateFlow(-1)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    val tasksPagingDataFlow: Flow<PagingData<Task>> = uiState.flatMapLatest {
        taskRepository
            .getAllTask(
                it.categoryFilter,
                it.collectionFilter,
                it.statusFilter
            )
    }.cachedIn(viewModelScope)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var deletedTask: Task? = null

    private val _categoryCollections = MutableStateFlow<List<String>>(emptyList())
    val categoryCollections: StateFlow<List<String>> get() = _categoryCollections.asStateFlow()

    val categories = taskCategoryRepository.getAllCategory()
        .map {
            it.map { category ->
                category.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    val collections = taskCollectionRepository.getAllCollection()
        .map {
            it.map { collection ->
                collection.title
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            emptyList()
        )

    val accept: (TasksUiEvent) -> Unit

    private var searchJob: Job? = null

    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState get() = _dialogState.asStateFlow()

    init {
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
                    setDialogState(
                        shouldShow = true,
                        dialog = ConfirmationDialog(
                            titleText = "Delete Task",
                            descriptionText = "Are you sure you want to delete this task?",
                            confirmText = "Yes, delete",
                            cancelText = "No, cancel",
                            action = ConfirmActions.DeleteTask(task = action.task)
                        )
                    )
                }

                is TasksUiEvent.OnDeleteConfirm -> {
                    deletedTask = action.task
                    viewModelScope.launch {
                        /*if (action.shouldPopBackStack) {
                            sendUiEvent(UiEvent.PopBackStack)
                        }*/
                        taskRepository.deleteTask(action.task)
                        deletedTask?.let { dueTaskAlarmScheduler.cancel(it) }
                        sendUiEvent(
                            UiEvent.ShowSnackBar(
                                "Task deleted!",
                                SnackBarActions.UNDO
                            )
                        )
                    }
                }

                is TasksUiEvent.OnUndoDeleteClick -> {
                    deletedTask?.let { task ->
                        viewModelScope.launch {
                            taskRepository.reInsertTask(task)
                            val timeNow = Clock.System.now()
                            val dueDate = Instant.fromEpochMilliseconds(task.dueDate)
                            if (timeNow <= dueDate) {
                                dueTaskAlarmScheduler.schedule(task)
                            }
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

        viewModelScope.launch(Dispatchers.IO) {
            taskCategoryRepository.getAllCategory()
                .collectLatest { taskCategories ->
                    _uiState.update {
                        it.copy(
                            categories = taskCategories,
                            categoryFilter = taskCategories.first().title
                        )
                    }
                }
        }
    }

    fun setDialogState(shouldShow: Boolean, dialog: AchivitDialog? = null) {
        _dialogState.update {
            it.copy(shouldShow = shouldShow, dialog = dialog)
        }
    }

    fun getCategoryCollections(categoryTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskCategoryRepository.getCategoryWithCollectionsByTitle(categoryTitle)
                .collectLatest { listOfCategoryWithCollection ->
                    listOfCategoryWithCollection.map { categoryWithCollections ->
                        val collectionList = categoryWithCollections.collections.map { it.title }
                        _categoryCollections.emit(collectionList)
                    }
                }
        }
    }

    fun setIsCompleted(task: Task, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(
                task.copy(
                    isCompleted = isChecked,
                    status = if (isChecked) TaskStatus.COMPLETED else TaskStatus.IN_PROGRESS
                )
            )
        }
    }

    private fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500L)
            taskRepository.searchTasksByTitle(query).onEach { result ->
                if (result is AchivitResult.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data)
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
                if (result is AchivitResult.Success) {
                    _uiState.update {
                        it.copy(tasksItems = result.data)
                    }
                }
            }.launchIn(viewModelScope)
            _uiEvent.emit(UiEvent.ScrollToTop)
        }
        // Todo: Add query to search history
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

    fun addNewTask(
        categoryTitle: String,
        collectionTitle: String,
        taskTitle: String,
        taskDescription: String,
        dueDate: Long,
    ) {

        created = Clock.System.now().toEpochMilliseconds()
        /*val category = getCategoryEntry(categoryTitle)
        val collection = getCollectionEntry(collectionTitle, categoryTitle)*/
        val newTask = getNewTaskEntry(
            taskTitle,
            taskDescription,
            created = created,
            dueDate = dueDate,
            collectionTitle,
            categoryTitle
        )

        viewModelScope.launch(Dispatchers.IO) {
            val insertedTaskId = taskRepository.insertAndGetTaskId(newTask)

            dueTaskAlarmScheduler.schedule(
                newTask.copy(id = insertedTaskId)
            )
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
        categoryTitle: String,
    ): Task {
        return Task(
            title = taskTitle,
            description = taskDescription,
            created = created,
            dueDate = dueDate,
            collectionTitle = collectionTitle,
            categoryTitle = categoryTitle
        )
    }

    // Input title validation
    fun isEntryValid(taskTitle: String, chipCount: Int): Boolean {
        return taskTitle.isNotBlank() && chipCount > 0
    }

    /*fun retrieveTask(id: Int): Flow<Task> {
        return taskRepository.getTask(id).mapLatest {
            it.data!!
        }
    }*/

    fun deleteTask(task: Task) {
        Log.d("Delete task", "called in VM, ${task.title}")
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
        val category = _uiState.value.categories[checkedId]
        _uiState.update {
            it.copy(
                checkedCategoryFilterChipId = checkedId,
                categoryFilter = category.title,
                checkedCollectionFilterChipId = -1
            )
        }
    }

    fun setCheckedCollectionChip(checkedId: Int) {
        val collection = categoryCollections.value[checkedId]
        _uiState.update {
            it.copy(
                checkedCollectionFilterChipId = checkedId,
                collectionFilter = collection
            )
        }
    }

    fun setCheckedStatusChip(checkedId: Int, status: String) {
        // Todo: Update the taskItems in the UI state object
//            val filteredTaskItems = taskRepository.getFilteredTasks()
        _uiState.update {
            it.copy(
                statusFilterId = checkedId,
                statusFilter = status.fromModifiedStatusText() as TaskStatus,
            )
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

data class DialogState(
    val shouldShow: Boolean = false,
    val dialog: AchivitDialog? = null
)

interface AchivitDialog {
    val title: String?
    val description: String?
    val dismissLabel: String?
    val confirmLabel: String?
}

class ConfirmationDialog(
    titleText: String,
    descriptionText: String,
    confirmText: String,
    cancelText: String,
    val action: ConfirmActions
) : AchivitDialog {
    override val title: String = titleText
    override val description: String = descriptionText
    override val dismissLabel: String = cancelText
    override val confirmLabel: String = confirmText
}

sealed interface ConfirmActions {
    data class DeleteTask(val task: Task) : ConfirmActions
}