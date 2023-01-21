package com.godzuche.achivitapp.feature_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.data.local.entity.TaskCategory
import com.godzuche.achivitapp.core.data.local.relations.CollectionWithTasks
import com.godzuche.achivitapp.core.domain.repository.CategoryRepository
import com.godzuche.achivitapp.core.domain.repository.CollectionRepository
import com.godzuche.achivitapp.core.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_tasks.presentation.util.TaskFilter
import com.godzuche.achivitapp.feature_tasks.presentation.util.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> get() = _uiState.asStateFlow()

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

        viewModelScope.launch(context = Dispatchers.IO) {
            categoryRepository.getAllCategory()
                .collectLatest { taskCategories ->
                    _uiState.update {
                        it.copy(categories = taskCategories)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val categoryAndCollectionsWithTasksPairs =
                emptyList<Pair<TaskCategory, List<CollectionWithTasks>>>().toMutableList()

            _uiState.collectLatest {
                // To avoid duplicate category entries in the list
                categoryAndCollectionsWithTasksPairs.clear()
                it.categories.forEach { category ->

                    collectionRepository.getCollectionsWithTasksByCategoryTitle(category.title)
                        .collectLatest { collectionsWithTasks ->
                            categoryAndCollectionsWithTasksPairs.add(category to collectionsWithTasks)
                        }
                }
                _uiState.update { state ->
                    state.copy(
                        categoryAndCollectionsWithTasksPairs = categoryAndCollectionsWithTasksPairs
                    )
                }
            }

        }

    }

}