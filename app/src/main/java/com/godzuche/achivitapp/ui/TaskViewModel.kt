package com.godzuche.achivitapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.data.TaskDao
import com.godzuche.achivitapp.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {
    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTask: StateFlow<List<Task>> = _allTasks
    val bottomSheetAction = MutableStateFlow<String>("")
    val bottomSheetTaskId = MutableStateFlow<Int>(-1)

    init {
        viewModelScope.launch {
            taskDao.getAllTasks().collect {
                _allTasks.emit(it)
            }
        }
    }

    private fun insertTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun addNewTask(taskTitle: String, taskDescription: String) {
        val newTask = getNewTaskEntry(taskTitle, taskDescription)
        insertTask(newTask)
    }

    private fun getNewTaskEntry(taskTitle: String, taskDescription: String): Task {
        return Task(
            title = taskTitle,
            description = taskDescription
        )
    }

    // Input title validation
    fun isEntryValid(taskTitle: String): Boolean {
        return taskTitle.isNotBlank()
    }

    fun retrieveTask(id: Int): Flow<Task> {
        return taskDao.getTask(id)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun undoDelete(task: Task) {
        insertTask(task)
    }

    fun updateTask(taskId: Int, taskTitle: String, taskDescription: String) {
        val updatedTask = getUpdatedTaskEntry(taskId, taskTitle, taskDescription)
        updateTask(updatedTask)
    }

    private fun getUpdatedTaskEntry(taskId: Int, taskTitle: String, taskDescription: String): Task {
        return Task(
            id = taskId,
            title = taskTitle,
            description = taskDescription
        )
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

}

class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}