@file:OptIn(ExperimentalCoroutinesApi::class)

package com.godzuche.achivitapp.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentAddTaskCategoryBinding
import com.godzuche.achivitapp.presentation.tasks.task_list.TasksViewModel
import com.godzuche.achivitapp.presentation.tasks.util.DialogTitle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCategoryCollectionDialogFragment : DialogFragment() {

    @ExperimentalCoroutinesApi
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private val addCategoryCollectionViewModel: AddCategoryCollectionViewModel by viewModels()

    private val navigationArgs: AddCategoryCollectionDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogTitle = navigationArgs.dialogTitle

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setCancelable(false)
            .setOnKeyListener { _, keyCode, event ->
                // This is the only way to intercept the back button press in DialogFragment.
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    close()
                    true
                } else false
            }
            .create()
        val binding = FragmentAddTaskCategoryBinding.inflate(dialog.layoutInflater)

        when (navigationArgs.dialogTitle) {
            DialogTitle.CATEGORY -> {
                binding.ilDropDown.visibility = View.GONE
            }

            DialogTitle.COLLECTION -> {
                binding.ilDropDown.visibility = View.VISIBLE
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        tasksViewModel.categories.collectLatest { categoryList ->
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.list_item_category,
                                categoryList
                            )
                            val exposedDropDown =
                                binding.ilDropDown.editText as? AutoCompleteTextView
                            exposedDropDown?.apply {
                                setAdapter(adapter)
                                /*if (categoryList.isNotEmpty()) {
                                    val initialSelection = adapter.getItem(0).toString()
                                    setText(initialSelection, false)
                                }*/
                            }
                        }
                    }
                }
            }
        }

        binding.bind()

        return dialog.apply {
            setView(binding.root)
        }
    }

    private fun FragmentAddTaskCategoryBinding.bind() {
        btSave.setOnClickListener {
            val title: String = ilCategory.editText?.text.toString()
            val exposedDropDown = ilDropDown.editText as? MaterialAutoCompleteTextView
            val categoryTitle = exposedDropDown?.text.toString()
            if (this.isEntryValid(title, categoryTitle)) {
                when (navigationArgs.dialogTitle) {
                    DialogTitle.CATEGORY -> {
                        addCategoryCollectionViewModel.addNewCategory(title = title)
                        close()
                    }

                    DialogTitle.COLLECTION -> {
                        addCategoryCollectionViewModel.addNewCollection(
                            title = title,
                            categoryTitle = categoryTitle
                        )
                        close()
                    }
                }
            } else {
                ilCategory.error = if (ilCategory.editText?.text.isNullOrBlank()) {
                    "Please enter a title"
                } else null
                ilDropDown.error = if (ilDropDown.editText?.text.isNullOrBlank()) {
                    "Please select a category"
                } else null
            }
        }
    }

    private fun FragmentAddTaskCategoryBinding.isEntryValid(
        title: String,
        categoryTitle: String,
    ): Boolean {
        return if (ilDropDown.isVisible) {
            title.isNotBlank() && categoryTitle.isNotBlank()
        } else title.isNotBlank()
    }

    private fun close() {
        findNavController().popBackStack()
    }
}