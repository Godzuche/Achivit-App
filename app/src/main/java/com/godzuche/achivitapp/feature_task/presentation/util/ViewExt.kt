package com.godzuche.achivitapp.feature_task.presentation.util

import androidx.appcompat.widget.SearchView
import com.godzuche.achivitapp.databinding.FragmentSearchBinding


inline fun SearchView.onQueryTextChange(
    binding: FragmentSearchBinding,
    crossinline searchListener: (String) -> Unit,
    crossinline onSearchListener: (String) -> Unit,
) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
//                binding.recyclerViewTasksList.scrollToPosition(0)
                searchListener(it)
                this@onQueryTextChange.clearFocus()
            }
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            onSearchListener(newText.orEmpty())
            return true
        }

    })
}