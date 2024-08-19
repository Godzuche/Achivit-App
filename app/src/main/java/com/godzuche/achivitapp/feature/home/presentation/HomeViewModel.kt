package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.domain.model.TaskStatus
import com.godzuche.achivitapp.core.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.core.domain.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val taskCategoryRepository: TaskCategoryRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

    val isOffline = networkMonitor.isOnlineFlow
        .map(Boolean::not)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    init {
        getTodayTasks()

        getTaskStatusCount(
            status = TaskStatus.NONE,
            statusOverviewType = TaskStatusOverview.None::class,
        )
        getTaskStatusCount(
            status = TaskStatus.TODO,
            statusOverviewType = TaskStatusOverview.Todo::class,
        )
        getTaskStatusCount(
            status = TaskStatus.IN_PROGRESS,
            statusOverviewType = TaskStatusOverview.InProgress::class,
        )
        getTaskStatusCount(
            status = TaskStatus.RUNNING_LATE,
            statusOverviewType = TaskStatusOverview.RunningLate::class,
        )
        getTaskStatusCount(
            status = TaskStatus.COMPLETED,
            statusOverviewType = TaskStatusOverview.Completed::class,
        )

        viewModelScope.launch {
            taskCategoryRepository.getCategoryWithCollectionsAndTasks()
                .collectLatest { categoryWithCollectionsAndTasks ->
                    _uiState.update {
                        it.copy(categoryWithCollectionsAndTasks = categoryWithCollectionsAndTasks)
                    }
                }

        }

    }

    private fun getTodayTasks() {
        viewModelScope.launch {
            taskRepository.getTodayTasks().collectLatest { tasks ->
                _uiState.update {
                    it.copy(todayTasks = tasks)
                }
            }
        }
    }

    private fun <T : TaskStatusOverview> getTaskStatusCount(
        status: TaskStatus,
        statusOverviewType: KClass<T>,
    ) {
        viewModelScope.launch {
            taskRepository.getTasksCountByStatus(status = status).collectLatest { count ->
                _uiState.update {
                    val updatedOverview = when (statusOverviewType) {
                        TaskStatusOverview.None::class -> {
                            (it.noneStatusOverview as TaskStatusOverview.None).copy(count = count)
                        }

                        TaskStatusOverview.Todo::class -> {
                            (it.todoStatusOverview as TaskStatusOverview.Todo).copy(count = count)
                        }

                        TaskStatusOverview.InProgress::class -> {
                            (it.inProgressStatusOverview as TaskStatusOverview.InProgress).copy(
                                count = count
                            )
                        }

                        TaskStatusOverview.RunningLate::class -> {
                            (it.runningLateStatusOverview as TaskStatusOverview.RunningLate).copy(
                                count = count
                            )
                        }

                        TaskStatusOverview.Completed::class -> {
                            (it.completedStatusOverview as TaskStatusOverview.Completed).copy(count = count)
                        }

                        else -> throw IllegalArgumentException("Unknown TaskStatusOverview class")
                    }
                    it.updateOverview(updatedOverview)
                }
            }
        }
    }

    private fun HomeUiState.updateOverview(updatedOverview: TaskStatusOverview): HomeUiState {
        return when (updatedOverview) {
            is TaskStatusOverview.None -> copy(noneStatusOverview = updatedOverview)

            is TaskStatusOverview.Todo -> copy(todoStatusOverview = updatedOverview)

            is TaskStatusOverview.InProgress -> copy(inProgressStatusOverview = updatedOverview)

            is TaskStatusOverview.RunningLate -> copy(runningLateStatusOverview = updatedOverview)

            is TaskStatusOverview.Completed -> copy(completedStatusOverview = updatedOverview)

        }
    }

    fun dismissPermissionDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

}