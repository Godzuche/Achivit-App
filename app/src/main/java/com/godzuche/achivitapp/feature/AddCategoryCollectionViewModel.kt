package com.godzuche.achivitapp.feature

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.core.domain.model.TaskCategory
import com.godzuche.achivitapp.core.domain.model.TaskCollection
import com.godzuche.achivitapp.core.domain.repository.TaskCategoryRepository
import com.godzuche.achivitapp.core.domain.repository.TaskCollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryCollectionViewModel @Inject constructor(
    private val taskCategoryRepository: TaskCategoryRepository,
    private val taskCollectionRepository: TaskCollectionRepository
) : ViewModel() {


    fun addNewCategory(title: String) {
        val created: Long = Calendar.getInstance().timeInMillis
        viewModelScope.launch(Dispatchers.IO) {
            taskCategoryRepository.insertCategory(
                TaskCategory(
                    title = title,
                    created = created
                )
            )
        }
    }

    fun addNewCollection(title: String, categoryTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskCollectionRepository.insertCollection(
                TaskCollection(
                    title = title,
                    categoryTitle = categoryTitle
                )
            )
        }
    }
}