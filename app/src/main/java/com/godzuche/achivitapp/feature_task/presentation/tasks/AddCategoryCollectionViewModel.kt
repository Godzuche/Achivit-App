package com.godzuche.achivitapp.feature_task.presentation.tasks

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategory
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.domain.repository.CategoryRepository
import com.godzuche.achivitapp.feature_task.domain.repository.CollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCategoryCollectionViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {


    fun addNewCategory(title: String) {
        val created: Long = Calendar.getInstance().timeInMillis
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.insertCategory(
                TaskCategory(
                    title = title,
                    created = created
                )
            )
        }
    }

    fun addNewCollection(title: String, categoryTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionRepository.insertCollection(
                TaskCollection(
                    title = title,
                    categoryTitle = categoryTitle
                )
            )
        }
    }
}