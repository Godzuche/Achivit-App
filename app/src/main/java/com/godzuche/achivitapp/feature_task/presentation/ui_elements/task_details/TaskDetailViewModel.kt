package com.godzuche.achivitapp.feature_task.presentation.ui_elements.task_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.domain.use_case.GetTask
import com.godzuche.achivitapp.feature_task.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskDetailUseCase: GetTask,
    private val repositoryImpl: TaskRepository,
) : ViewModel() {

    private val taskId = MutableStateFlow(-1)

/*
    private var _uiState: MutableStateFlow<TaskUiState>
    val uiState = _uiState.asStateFlow()
*/

    val detail = taskId.transformLatest { taskId ->
        emitAll(getTaskDetailUseCase(taskId).map { result ->
            result.data
        })
    }.stateIn(scope = viewModelScope, started = WhileSubscribed(), initialValue = null)

    private var deletedTask: Task? = null

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept: (TaskUiEvent) -> Unit

    init {

        accept = { action ->
            when (action) {
                is TaskUiEvent.OnRetrieveTask -> {
//                    retrieveTask(action.taskId)
                    viewModelScope.launch { taskId.emit(action.taskId) }
                }
                is TaskUiEvent.OnDeleteTask -> {
                    deletedTask = action.task
                    /*sendUiEvent(UiEvent.ShowSnackBar(
                        message = "Task deleted",
                        action = SnackBarActions.UNDO
                    ))*/
                    deleteTask(task = action.task)
                    sendUiEvent(UiEvent.PopBackStack)
                }
                is TaskUiEvent.OnUndoDeleteClick -> {
                    viewModelScope.launch {
                        deletedTask?.let { task ->
                            repositoryImpl.reInsertTask(task)
                        }
                    }
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

/*    private fun retrieveTask(taskId: Int) {
        repositoryImpl.getTask(taskId).mapLatest {
            it.data!!
        }
    }*/

    private fun deleteTask(task: Task) {
        viewModelScope.launch { repositoryImpl.deleteTask(task) }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
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
            repositoryImpl.updateTask(task)
        }
    }


}