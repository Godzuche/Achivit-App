@file:OptIn(ExperimentalCoroutinesApi::class)

package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.databinding.FragmentAddTaskCategoryBinding
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_task.presentation.util.DialogTitle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class AddCategoryCollectionFragment : DialogFragment() {

    @ExperimentalCoroutinesApi
    private val viewModel: TasksViewModel by viewModels()

    private val navigationArgs: AddCategoryCollectionFragmentArgs by navArgs()

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
            if (isEntryValid(title)) {
                when (navigationArgs.dialogTitle) {
                    DialogTitle.CATEGORY -> {
                        viewModel.addNewCategory(title = title)
                        close()
                    }
                    DialogTitle.COLLECTION -> {
//                        viewModel.addNewCollection(title = title)
                        close()
                    }
                }
            }
        }
    }

    private fun isEntryValid(title: String): Boolean {
        return title.isNotBlank()
    }

    private fun close() {
        findNavController().popBackStack()
    }
}