package com.godzuche.achivitapp.feature_home.presentation.core.presentation

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.godzuche.achivitapp.data.local.entity.TaskCategory
import com.godzuche.achivitapp.data.local.entity.TaskCollection
import com.godzuche.achivitapp.domain.repository.CategoryRepository
import com.godzuche.achivitapp.domain.repository.CollectionRepository
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