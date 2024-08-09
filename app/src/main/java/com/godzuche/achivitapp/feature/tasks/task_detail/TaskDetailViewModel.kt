package com.godzuche.achivitapp.feature.tasks.task_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.common.AchivitResult
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.core.domain.usecase.GetTaskUseCase
import com.godzuche.achivitapp.feature.tasks.task_list.AchivitDialog
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmActions
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmationDialog
import com.godzuche.achivitapp.feature.tasks.task_list.DialogState
import com.godzuche.achivitapp.feature.tasks.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskUseCase: GetTaskUseCase,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val taskId = MutableStateFlow(-1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val detail = taskId.transformLatest { taskId ->
        emitAll(
            getTaskUseCase(taskId).map { result ->
                /*if (result is AchivitResult.Success) {
                    result.data
                } else null*/
                when (result) {
                    is AchivitResult.Loading -> TaskDetailUiState.Loading
                    is AchivitResult.Success -> TaskDetailUiState.Success(data = result.data)
                    is AchivitResult.Error -> TaskDetailUiState.Error(exception = result.exception)
                }
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(),
        initialValue = TaskDetailUiState.Loading
    )

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept: (TaskUiEvent) -> Unit

    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState get() = _dialogState.asStateFlow()

    init {

        accept = { action ->
            when (action) {
                is TaskUiEvent.OnRetrieveTask -> {
//                    retrieveTask(action.taskId)
                    viewModelScope.launch { taskId.emit(action.taskId) }
                }

                is TaskUiEvent.OnDeleteTask -> {
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

                is TaskUiEvent.OnUpdateTask -> {
                }

                is TaskUiEvent.OnUpdateTaskClick -> {
                }

                is TaskUiEvent.OnNavigateUp -> {
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }

    fun setDialogState(shouldShow: Boolean, dialog: AchivitDialog? = null) {
        _dialogState.update {
            it.copy(shouldShow = shouldShow, dialog = dialog)
        }
    }

    private fun deleteTask(task: Task) {
        viewModelScope.launch { taskRepository.deleteTask(task) }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    // Todo: Create a method to update just the due date instead
    /*    fun updateTask(
            taskId: Int,
            taskTitle: String,
            taskDescription: String,
            dueDate: Long,
            collectionTitle: String,
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
            updateTask(updatedTask)
        }

        private fun getUpdatedTaskEntry(
            taskId: Int,
            taskTitle: String,
            taskDescription: String,
            dueDate: Long,
            collectionTitle: String,
            categoryTitle: String
        ): Task {
            return Task(
                id = taskId,
                title = taskTitle,
                description = taskDescription,
                created = detail.value?.created!!,
                dueDate = dueDate,
                collectionTitle = collectionTitle,
                categoryTitle = categoryTitle
            )
        }*/

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

}

sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState
    data class Success(val data: Task) : TaskDetailUiState
    data class Error(val exception: Throwable?) : TaskDetailUiState
}