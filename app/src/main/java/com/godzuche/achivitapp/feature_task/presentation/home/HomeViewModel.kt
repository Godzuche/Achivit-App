package com.godzuche.achivitapp.feature_task.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.feature_task.domain.repository.CategoryRepository
import com.godzuche.achivitapp.feature_task.domain.repository.CollectionRepository
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.util.TaskFilter
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            taskRepository.getFilteredTasks(filter = TaskFilter(status = TaskStatus.NONE)).map {
                it.size
            }.collectLatest { noneStatusCount ->
                _uiState.update {
                    it.copy(
                        noneStatusCount = noneStatusCount
                    )
                }
                Log.d("HomeViewModel", "retrieved $noneStatusCount todo tasks")
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
                Log.d("HomeViewModel", "retrieved $todosCount todo tasks")
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
                    Log.d("HomeViewModel", "retrieved $inProgressCount tasks in progress")
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
                    Log.d("HomeViewModel", "retrieved $lateTasksCount tasks in progress")
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
                    Log.d("HomeViewModel", "retrieved $completedTasksCount tasks in progress")
                }
        }

        viewModelScope.launch(context = Dispatchers.IO) {
            categoryRepository.getCategoriesWithCollections()
                .map {
                    it.map { categoryWithCollections ->
                        categoryWithCollections.category.toTaskCategory() to categoryWithCollections.collections.map { it.toTaskCollection() }
                    }
                }.collectLatest { categoryWithCollectionsPairs ->
                    _uiState.update {
                        it.copy(categoryWithCollectionsPairs = categoryWithCollectionsPairs)
                    }
                }
        }

    }

}