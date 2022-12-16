package com.godzuche.achivitapp.feature_task.presentation.modal_bottom_sheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollectionEntity
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.CategoryRepository
import com.godzuche.achivitapp.feature_task.domain.repository.CollectionRepository
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.ui_state.ModalBottomSheetUiState
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import com.godzuche.achivitapp.feature_task.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModalBottomSheetViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val taskId = MutableStateFlow(-1)
    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    val uiStateFlow: StateFlow<ModalBottomSheetUiState>

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
        ) { a, b, c ->
            ModalBottomSheetUiState(
                bottomSheetAction = a,
                task = b,
                id = c
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
    ) {
        val updatedTask =
            getUpdatedTaskEntry(taskId, taskTitle, taskDescription, dueDate, collectionTitle)
        updateTask(updatedTask)
    }

    private fun getUpdatedTaskEntry(
        taskId: Int,
        taskTitle: String,
        taskDescription: String,
        dueDate: Long,
        collectionTitle: String,
    ): Task {
        return Task(
            id = taskId,
            title = taskTitle,
            description = taskDescription,
            dueDate = dueDate,
            collectionTitle = collectionTitle
        )
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun getCollectionCategory(collectionTitle: String, onGetCategoryTitle: ((String) -> Unit)) {
        viewModelScope.launch {
            collectionRepository.getCollection(collectionTitle).collectLatest {
                onGetCategoryTitle(it.categoryTitle)
            }
        }
    }

    fun getCategoryCollections(categoryTitle: String, onGetCollections: (List<String>) -> Unit) {
        viewModelScope.launch {
            categoryRepository.getCategoryWithCollectionsByTitle(categoryTitle).map { list ->
                list.map { category ->
                    onGetCollections(category.collections.map { it.title })
                }
            }
        }
    }

}