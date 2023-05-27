package com.godzuche.achivitapp.feature.feed.task_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.data.DueTaskAndroidAlarmScheduler
import com.godzuche.achivitapp.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.domain.repository.CategoryRepository
import com.godzuche.achivitapp.domain.repository.CollectionRepository
import com.godzuche.achivitapp.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature.feed.ui_state.TasksUiState
import com.godzuche.achivitapp.feature.feed.util.Routes
import com.godzuche.achivitapp.feature.feed.util.SnackBarActions
import com.godzuche.achivitapp.feature.feed.util.TaskStatus
import com.godzuche.achivitapp.feature.feed.util.UiEvent
import com.godzuche.achivitapp.feature.home.presentation.fromChipText
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
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val app: Application,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
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

    val categoryCollections: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

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

    /*    private val _filter = MutableStateFlow(TaskFilter())
        val filter: StateFlow<TaskFilter> = _filter.asStateFlow()*/

    val accept: (TasksUiEvent) -> Unit

    private var searchJob: Job? = null


    init {


        var noScrollPosition: Int = 0
        var lastScrollPosition: Int = 0

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


        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.getAllCategory()
                .collectLatest { taskCategories ->
                    _uiState.update {
                        it.copy(categories = taskCategories)
                    }
                }
        }


    }

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

    /*   fun filter(
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
       }*/

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

        created = Calendar.getInstance().timeInMillis
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
            val insertedTaskId = taskRepository.insertAndGetTask(newTask)

            dueTaskAlarmScheduler.schedule(
                newTask.copy(id = insertedTaskId)
            )

            Timber.tag("DueTask").d("TasksViewModel Set at %s", dueDate.toString())
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

    fun setCheckedCategoryChip(checkedId: Int, categoryTitle: String) {
        // Todo: Update the taskItems in the UI state object
//            val filteredTaskItems = taskRepository.getFilteredTasks()
        _uiState.update {
            it.copy(
                checkedCategoryFilterChipId = checkedId,
                categoryFilter = categoryTitle
            )
        }
    }

    fun setCheckedCollectionChip(checkedId: Int, collectionTitle: String) {
        // Todo: Update the taskItems in the UI state object
//            val filteredTaskItems = taskRepository.getFilteredTasks()
        _uiState.update {
            it.copy(
                checkedCollectionFilterChipId = checkedId,
                collectionFilter = collectionTitle
            )
        }
    }

    fun setCheckedStatusChip(checkedId: Int, status: String) {
        // Todo: Update the taskItems in the UI state object
//            val filteredTaskItems = taskRepository.getFilteredTasks()
        _uiState.update {
            it.copy(
                statusFilterId = checkedId,
                statusFilter = status.fromChipText() as TaskStatus,
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