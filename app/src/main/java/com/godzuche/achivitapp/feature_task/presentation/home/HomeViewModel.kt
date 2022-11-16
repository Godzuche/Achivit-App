package com.godzuche.achivitapp.feature_task.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.domain.repository.TaskRepository
import com.godzuche.achivitapp.feature_task.presentation.util.TaskFilter
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    val todoTasks: StateFlow<Int>
        get() =
            repository.getFilteredTasks(TaskFilter(status = TaskStatus.TODO))
                .map {
                    it.size
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(),
                    0
                )
}