package com.godzuche.achivitapp.ui

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
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
import com.godzuche.achivitapp.databinding.ModalBottomSheetContentBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TaskViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ModalBottomSheet : BottomSheetDialogFragment() {
    private var dateSelection: Long = 0L
    private var formattedDateString = ""

    private var mHour = 0
    private var mMinute = 0

    private var taskId: Int? = null
    private val viewModel: TaskViewModel by activityViewModels()

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        Log.d("ModalBottomSheetDialog Fragment: ", "onCreateView")

        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val timePicker = MaterialTimePicker.Builder()
//            .setTimeFormat(clockFormat)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour((Calendar.getInstance()[Calendar.HOUR_OF_DAY]))
            .setMinute(Calendar.getInstance()[Calendar.MINUTE])
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Select time")
            .build()

        val materialDatePicker = MaterialDatePicker.Builder.datePicker()

        val datePicker = materialDatePicker
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        timePicker.apply {
            addOnCancelListener {
                activity?.supportFragmentManager?.let { it1 ->
                    datePicker.show(it1,
                        "date_picker_tag")
                }
            }
//            isCancelable = false
            addOnNegativeButtonClickListener {
                activity?.supportFragmentManager?.let { it1 ->
                    datePicker.show(it1,
                        "date_picker_tag")
                }
            }

            addOnPositiveButtonClickListener {
                binding.chipGroupTime.removeAllViews()
                mMinute = this.minute

                val timeSuffix: String

                mHour = when {
                    hour == 12 -> {
                        timeSuffix = "PM"
                        hour
                    }
                    hour > 12 -> {
                        timeSuffix = "PM"
                        hour - 12
                    }
                    else -> {
                        timeSuffix = "AM"
                        hour
                    }
                }

                Log.d("Time Picker", "Hours = ${this.hour} | Minutes = ${this.minute}")
                Log.d("Time Picker", "MHours = $mHour | MMinutes = $mMinute")

                val chip = Chip(requireContext(), null, R.style.Widget_App_Chip_Input)
                chip.apply {
                    text = getString(R.string.date_time, formattedDateString,
                        mHour,
                        mMinute,
                        timeSuffix)
                    isCloseIconVisible = true
                    this.setOnCloseIconClickListener {
                        binding.chipGroupTime.removeView(this)
                    }
                    this.setOnClickListener {
                        activity?.supportFragmentManager?.let { it1 ->
                            materialDatePicker.setSelection(dateSelection)
                                .setTitleText("Select date")
                                .build()
                                .show(
                                    it1,
                                    "date_picker_tag"
                                )
                        }
                    }
                }

                binding.chipGroupTime.addView(chip)

            }
        }

        datePicker.apply {
            addOnPositiveButtonClickListener {
                //E - day name, MMM - month in 3 letters, d for day....Tue Dec 10
                val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
                // Calender instance
                val calender = Calendar.getInstance()
                dateSelection = this.selection!!
                Log.d("Date Picker", "Date Selection = $dateSelection")
                formattedDateString = formatter.format(this.selection)
                // then open time picker
                activity?.supportFragmentManager?.let { it1 ->
                    timePicker.show(it1,
                        "time_picker_tag")
                }
            }
            this.isCancelable = false
            addOnDismissListener {
                //
            }
            addOnNegativeButtonClickListener {
                //
            }

        }

        binding.btPickTime.setOnClickListener {

            activity?.supportFragmentManager?.let { it1 -> datePicker.show(it1, "date_picker_tag") }
        }

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

        Log.d("ModalBottomSheetDialog Fragment: ", "onViewCreated")

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

            dismiss()

            resetInputs()

        }
    }

    private fun addNewTask() {
        if (isEntryValid()) {
            viewModel.addNewTask(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString()
            )

            dismiss()

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

        Log.d("ModalBottomSheetDialog Fragment: ", "onResume")

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
        Log.d("ModalBottomSheetDialog Fragment: ", "onDestroy")
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

}