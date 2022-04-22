package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.databinding.FragmentAddTaskCategoryBinding
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi

class AddTaskCategoryFragment : DialogFragment() {

    @ExperimentalCoroutinesApi
    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add a new category")
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

        return dialog.apply {
            setView(binding.root)
        }
    }

    private fun close() {
        findNavController().popBackStack()
    }
}