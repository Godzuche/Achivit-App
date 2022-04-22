package com.godzuche.achivitapp.feature_task.presentation.ui_elements.modal_bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ModalBottomSheetContentBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.home.HomeFragment.Companion.NOT_SET
import com.godzuche.achivitapp.feature_task.presentation.util.task_frag_util.DateTimePickerUtil.formatDateTime
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var currentTask: Task
    private var sHour: Int = 0
    private var dateSelection: Long = 0L
    private var formattedDateString = ""
    private var mHour = 0
    private var mMinute = 0
    private var taskId: Long = NOT_SET

    private val viewModel: ModalBottomSheetViewModel by viewModels()
    private val activityViewModel: TasksViewModel by activityViewModels()

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: ModalBottomSheetArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = navigationArgs.taskId
        taskId = id
        viewModel.accept(ModalBottomSheetUiEvent.OnGetBottomSheetAction(taskId = id))
    }

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
/*        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H*/

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
                datePicker.show(childFragmentManager, "date_picker_tag")
            }
//            isCancelable = false
            addOnNegativeButtonClickListener {
                datePicker.show(childFragmentManager, "date_picker_tag")
            }

            addOnPositiveButtonClickListener {
                binding.chipGroupTime.removeAllViews()
                mMinute = minute
                sHour = hour

                val chip = Chip(requireContext(), null, R.style.Widget_App_Chip_Input)
                chip.apply {
                    text = formatDateTime(
                        minutes = minute,
                        hours = hour,
                        dateSelection = dateSelection
                    )
                    isCloseIconVisible = true
                    this.setOnCloseIconClickListener {
                        binding.chipGroupTime.removeView(this)
                    }
                }
                binding.chipGroupTime.addView(chip)
            }
        }

        datePicker.apply {
            addOnPositiveButtonClickListener {
                //E - day name, MMM - month in 3 letters, d for day....Tue Dec 10
                val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())

                dateSelection = this.selection!!
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

        bind(navigationArgs.taskId)
    }

    private fun bind(taskId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow.map { it.bottomSheetAction }.collectLatest { action ->
                    binding.tvHeader.text = action
                }
            }
        }

        if (taskId != -1L) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiStateFlow
                        .map { it.task }
                        .distinctUntilChanged()
                        .collectLatest { it ->
                            it?.let { task ->
                                currentTask = task
                                binding.apply {
                                    btSave.setOnClickListener {
                                        updateTask(taskId)
                                    }
                                    etTitle.setText(task.title)
                                    etDescription.setText(task.description)
                                    btSave.text = getString(R.string.update)

                                    val chip =
                                        Chip(requireContext(), null, R.style.Widget_App_Chip_Input)
                                    chip.apply {
                                        text = formatDateTime(
                                            minutes = task.minutes,
                                            hours = task.hours,
                                            dateSelection = task.date
                                        )
                                        isCloseIconVisible = true
                                        this.setOnCloseIconClickListener {
                                            binding.chipGroupTime.removeView(this)
                                        }
                                    }

                                    binding.chipGroupTime.apply {
                                        removeAllViews()
                                        addView(chip, 0)
                                    }
                                }
                            }
                        }
                }
            }
        } else if (taskId == -1L) {
            (binding.ilCategory.editText as MaterialAutoCompleteTextView)
                .setText("My Tasks", false)
            binding.btSave.apply {
                text = getString(R.string.save)
                setOnClickListener {
                    addNewTask()
                }
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.etTitle.text.toString(),
            binding.chipGroupTime.childCount
        )
    }

    private fun updateTask(id: Long) {
        if (isEntryValid()) {
            binding.apply {
                if (isTaskEqual(currentTask,
                        etTitle.text.toString(),
                        etDescription.text.toString(),
                        (chipGroupTime.getChildAt(0) as Chip).text.toString())
                ) {
                    Toast.makeText(requireContext(), "Task is unchanged!", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    if ((chipGroupTime.getChildAt(0) as Chip).text.toString() == formatDateTime(
                            currentTask.minutes,
                            currentTask.hours,
                            currentTask.date)
                    ) {
                        Toast.makeText(requireContext(),
                            "Same date and time",
                            Toast.LENGTH_SHORT)
                            .show()

                        viewModel.updateTask(
                            id,
                            binding.etTitle.text.toString(),
                            binding.etDescription.text.toString(),
                            currentTask.date,
                            currentTask.hours,
                            currentTask.minutes
                        )

                    } else {
                        viewModel.updateTask(
                            id,
                            binding.etTitle.text.toString(),
                            binding.etDescription.text.toString(),
                            dateSelection,
                            sHour,
                            mMinute
                        )
                    }
                }
            }
            dismiss()
            resetInputs()
        }
    }

    private fun isTaskEqual(
        task1: Task,
        title: String,
        description: String,
        chipText: String,
    ): Boolean {
        return task1.title == title && task1.description == description && formatDateTime(task1.minutes,
            task1.hours,
            task1.date) == chipText
    }

    private fun addNewTask() {
        if (isEntryValid()) {
            activityViewModel.addNewTask(
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                dateSelection,
                sHour,
                mMinute
            )
            dismiss()
            resetInputs()
        }

    }

    private fun resetInputs() {
        binding.ilCategory.editText?.text?.clear()
        binding.ilTitle.editText?.text?.clear()
        binding.ilDescription.editText?.text?.clear()
        binding.chipGroupTime.removeAllViews()
    }

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item_category,
//            resources.getStringArray(R.array.drop_down_categories)
            listOf("My Tasks")
        )

        (binding.ilCategory.editText as? AutoCompleteTextView)
            ?.setAdapter(adapter)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

/*    companion object {
        const val TAG = "ModalBottomSheet"
    }*/

}