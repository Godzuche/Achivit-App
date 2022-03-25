package com.godzuche.achivitapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.TaskApplication
import com.godzuche.achivitapp.data.model.Task
import com.godzuche.achivitapp.databinding.ModalBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ModalBottomSheet : BottomSheetDialogFragment() {

    private var taskId: Int? = null
    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication)
                .database.taskDao()
        )
    }

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetTaskId.collectLatest {
                    taskId = it
                    taskId?.let { it1 ->
                        bind(it1)
                    }
                }
            }
        }

    }

    private fun bind(taskId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetAction.collectLatest {
                    binding.tvHeader.text = it
                }
            }
        }

        if (taskId != -1) {
            binding.btSave.setOnClickListener {
                updateTask()
            }
            var task: Task? = null
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.retrieveTask(taskId).collectLatest {
                        task = it
                        binding.apply {
                            etTitle.setText(task?.title)
                            etDescription.setText(task?.description)
                            btSave.text = "Update"
                        }
                    }
                }
            }

        } else if (taskId == -1) {
            (binding.ilCategory.editText as MaterialAutoCompleteTextView)
                .setText("My Tasks", false)
            binding.btSave.apply {
                text = "Save"
                setOnClickListener {
                    addNewTask()
                }
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.etTitle.text.toString()
        )
    }

    private fun updateTask() {
        if (isEntryValid()) {
            taskId?.let {
                viewModel.updateTask(
                    it,
                    binding.etTitle.text.toString(),
                    binding.etDescription.text.toString()
                )
            }

            val bottomSheetBehavior =
                (dialog as BottomSheetDialog).behavior

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            resetInputs()

        }
    }

    private fun addNewTask() {
        if (isEntryValid()) {
            viewModel.addNewTask(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString()
            )
            val bottomSheetBehavior =
                (dialog as BottomSheetDialog).behavior

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            resetInputs()

        }

    }

    private fun resetInputs() {
        binding.ilCategory.editText?.text?.clear()
        binding.ilTitle.editText?.text?.clear()
        binding.ilDescription.editText?.text?.clear()
    }

    override fun onResume() {
        super.onResume()

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item_category,
//            resources.getStringArray(R.array.drop_down_categories)
            listOf("My Tasks", "School", "Work")
        )

        (binding.ilCategory.editText as? AutoCompleteTextView)
            ?.setAdapter(adapter)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}