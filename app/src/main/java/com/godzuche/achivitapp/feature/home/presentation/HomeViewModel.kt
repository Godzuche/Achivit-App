package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.core.domain.util.NetworkMonitor
import com.godzuche.achivitapp.feature.tasks.util.TaskFilter
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getTodayTasks().collectLatest { tasks ->
                _uiState.update {
                    it.copy(todayTasks = tasks)
                }
            }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.NONE)).map {
                it.size
            }.collectLatest { noneStatusCount ->
                _uiState.update {
                    it.copy(
                        noneStatusCount = noneStatusCount
                    )
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.TODO)).map {
                it.size
            }.collectLatest { todosCount ->
                _uiState.update {
                    it.copy(
                        todosTaskCount = todosCount
                    )
                }
            }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.IN_PROGRESS))
                .map {
                    it.size
                }.collectLatest { inProgressCount ->
                    _uiState.update {
                        it.copy(
                            inProgressTaskCount = inProgressCount
                        )
                    }
                }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.RUNNING_LATE))
                .map {
                    it.size
                }.collectLatest { lateTasksCount ->
                    _uiState.update {
                        it.copy(
                            lateTasksCount = lateTasksCount
                        )
                    }
                }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.COMPLETED))
                .map {
                    it.size
                }.collectLatest { completedTasksCount ->
                    _uiState.update {
                        it.copy(
                            completedTasksCount = completedTasksCount
                        )
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            taskCategoryRepository.getCategoryWithCollectionsAndTasks()
                .collectLatest { categoryWithCollectionsAndTasks ->
                    _uiState.update {
                        it.copy(categoryWithCollectionsAndTasks = categoryWithCollectionsAndTasks)
                    }
                }

        }

    }

    fun dismissPermissionDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

}