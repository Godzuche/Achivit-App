package com.godzuche.achivitapp.feature.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.databinding.ModalBottomSheetContentBinding
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.task_list.TasksFragment.Companion.NOT_SET
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ModalBottomSheet : BottomSheetDialogFragment() {
    private var taskDueDate: Long = 0L
    private val mCalendar by lazy {
        Calendar.getInstance()
    }
    private var currentSystemDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    private lateinit var currentTask: Task
    private var mHour = 0
    private var mMinute = 0
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var dateSelection: Long = 0L
    private var taskId: Int = NOT_SET

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

        binding.btPickTime.setOnClickListener {
            showDatePicker()
        }

        bind(navigationArgs.taskId)
    }

    private fun showDatePicker() {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
        val datePicker = materialDatePicker
            .setTitleText("Select a ate")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.apply {
            addOnPositiveButtonClickListener {
//                val c = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
                dateSelection = it
                val cal = Calendar.getInstance()
                cal.timeInMillis = dateSelection
                mYear = cal[Calendar.YEAR]
                mMonth = cal[Calendar.MONTH]
                mDay = cal[Calendar.DAY_OF_MONTH]

                showTimePicker()
            }
            this.isCancelable = false
        }
        datePicker.show(childFragmentManager, "date_picker_tag")

    }

    private fun showTimePicker() {
        /*        val isSystem24Hour = is24HourFormat(requireContext())
         val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H*/

        val timePicker = MaterialTimePicker.Builder()
//            .setTimeFormat(clockFormat)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentSystemDateTime.hour)
            .setMinute(currentSystemDateTime.minute)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Select a time")
            .build()

        timePicker.apply {
            addOnCancelListener {
                showDatePicker()
            }
            addOnNegativeButtonClickListener {
                showDatePicker()
            }
            addOnPositiveButtonClickListener {
                binding.chipGroupTime.removeAllViews()
                mMinute = minute
                mHour = hour

                mCalendar.apply {
                    set(Calendar.YEAR, mYear)
                    set(Calendar.MONTH, mMonth)
                    set(Calendar.DAY_OF_MONTH, mDay)
                    set(Calendar.HOUR_OF_DAY, mHour)
                    set(Calendar.MINUTE, mMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                taskDueDate = mCalendar.timeInMillis

                val chip = Chip(requireContext(), null, R.style.Widget_App_Chip_Input)
                chip.apply {
                    text = taskDueDate.millisToString()
                    isCloseIconVisible = true
                    this.setOnCloseIconClickListener {
                        binding.chipGroupTime.removeView(this)
                    }
                }
                binding.chipGroupTime.addView(chip)
            }
        }
        timePicker.show(childFragmentManager, "time_picker_tag")
    }

    private fun Int.isValid(): Boolean {
        return this != -1
    }

    private fun bind(taskId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow.map { it.bottomSheetActionTitle }.collectLatest { action ->
                    binding.tvHeader.text = action
                }
            }
        }

        if (taskId.isValid()) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiStateFlow
                        .map { uiState -> uiState.task }
                        .distinctUntilChanged()
                        .collectLatest {
                            it?.let { task ->
                                currentTask = task
                                binding.apply {
                                    btSave.setOnClickListener {
                                        updateTask(taskId)
                                    }
                                    val collectionsExposedDropDown =
                                        binding.ilCollection.editText as MaterialAutoCompleteTextView
                                    val categoriesExposedDropDown =
                                        binding.ilCategory.editText as? MaterialAutoCompleteTextView
                                    binding.ilCategory.visibility = View.INVISIBLE
                                    categoriesExposedDropDown?.setText(task.categoryTitle)
                                    viewModel.getCategoryCollections(task.categoryTitle)
                                    launch {
                                        viewModel.categoryCollections.collectLatest { collectionList ->
                                            val adapter = ArrayAdapter(
                                                requireContext(),
                                                R.layout.list_item_category,
                                                collectionList
                                            )
                                            collectionsExposedDropDown.apply {
                                                setAdapter(adapter)
                                                /* if (collectionList.isNotEmpty()) {
                                                     val initialSelection =
                                                         adapter.getItem(0).toString()
                                                     setText(initialSelection, false)
                                                 }*/
                                                setText(task.collectionTitle, false)
                                            }
                                        }
                                    }
//                                        }
//                                    }
                                    etCollection.setText(task.collectionTitle)
                                    etTitle.setText(task.title)
                                    etDescription.setText(task.description)
                                    btSave.text = getString(R.string.update)

                                    val chip =
                                        Chip(requireContext(), null, R.style.Widget_App_Chip_Input)
                                    chip.apply {
                                        text = task.dueDate.millisToString()
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
        } else if (taskId.isValid().not()) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.categories.collectLatest { categoryList ->
                            val adapter = ArrayAdapter(
                                requireContext(),
                                R.layout.list_item_category,
                                categoryList
                            )
                            val exposedDropDown =
                                binding.ilCategory.editText as? MaterialAutoCompleteTextView
                            exposedDropDown?.apply {
                                if (categoryList.isNotEmpty() && taskId == NOT_SET) {
                                    val initialSelection = adapter.getItem(0).toString()
                                    setText(initialSelection, false)
                                    viewModel.getCategoryCollections(categoryTitle = initialSelection)
                                }
                                setAdapter(adapter)
                                setOnItemClickListener { parent, view, position, id ->
                                    viewModel.getCategoryCollections(categoryTitle = categoryList[position])
                                }
                            }
                        }
                    }

                    launch {
                        viewModel.categoryCollections.collectLatest { collectionList ->
                            val adapter = ArrayAdapter(
                                binding.ilCollection.context,
                                R.layout.list_item_category,
                                collectionList
                            )
                            val exposedDropDown =
                                binding.ilCollection.editText as MaterialAutoCompleteTextView
                            exposedDropDown.apply {
                                if (collectionList.isNotEmpty() && taskId == NOT_SET) {
                                    val initialSelection = adapter.getItem(0).toString()
                                    setText(initialSelection, false)
                                } else {
                                    setText("", false)
                                }
                                setAdapter(adapter)
                            }
                        }
                    }

                }
            }

            binding.ilCategory.visibility = View.VISIBLE
            binding.btSave.apply {
                text = getString(R.string.save)
                setOnClickListener {
                    addNewTask()
                }
            }
        }
    }

    private fun isEntryValid(): Boolean {
        val categoryText = binding.ilCategory.editText?.text
        val collectionText = binding.ilCollection.editText?.text
        return viewModel.isEntryValid(
            categoryText.toString(),
            collectionText.toString(),
            binding.etTitle.text.toString(),
            binding.chipGroupTime.childCount
        )
    }

    private fun updateTask(id: Int) {
        if (isEntryValid()) {
            binding.apply {
                if (
                    isTaskEqual(
                        currentTask,
                        etTitle.text.toString(),
                        etDescription.text.toString(),
                        (chipGroupTime.getChildAt(0) as Chip).text.toString()
                    )
                ) {
                    Toast.makeText(requireContext(), "Task is unchanged!", Toast.LENGTH_SHORT)
                        .show()

                } else {
                    if (
                        (chipGroupTime.getChildAt(0) as Chip).text.toString() == currentTask.dueDate.millisToString()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Time is unchanged!",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        viewModel.updateTask(
                            taskId = id,
                            taskTitle = binding.etTitle.text.toString(),
                            taskDescription = binding.etDescription.text.toString(),
                            dueDate = currentTask.dueDate,
                            collectionTitle = binding.etCollection.text.toString(),
                            categoryTitle = currentTask.categoryTitle,
                            shouldReschedule = false
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "This task has been successfully rescheduled!",
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.updateTask(
                            taskId = id,
                            taskTitle = binding.etTitle.text.toString(),
                            taskDescription = binding.etDescription.text.toString(),
                            dueDate = mCalendar.timeInMillis,
                            collectionTitle = binding.etCollection.text.toString(),
                            categoryTitle = currentTask.categoryTitle,
                            shouldReschedule = true
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
        return task1.title == title && task1.description == description && task1.dueDate.millisToString() == chipText
    }

    private fun addNewTask() {
        val categoryDropDown = binding.ilCategory.editText as? MaterialAutoCompleteTextView
        val collectionDropDown = binding.ilCollection.editText as? MaterialAutoCompleteTextView
        if (isEntryValid()) {
            activityViewModel.addNewTask(
                categoryDropDown?.text.toString(),
                collectionDropDown?.text.toString(),
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                mCalendar.timeInMillis
            )
            dismiss()
            resetInputs()
        } else {
            binding.ilCategory.error =
                if (categoryDropDown?.text.isNullOrBlank()) "Please select a category"
                else null
            binding.ilCollection.error =
                if (collectionDropDown?.text.isNullOrBlank()) "Please select a collection"
                else null
            binding.ilTitle.error =
                if (binding.ilTitle.editText?.text.isNullOrBlank()) "Please enter a title"
                else null
            if (binding.chipGroupTime.childCount <= 0) {
                Toast.makeText(requireContext(), "Please set due date and time", Toast.LENGTH_LONG)
                    .show()
            }
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

        /*viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.categories.collectLatest { categoryList ->
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.list_item_category,
                        categoryList
                    )
                    val exposedDropDown =
                        binding.ilCategory.editText as? MaterialAutoCompleteTextView
                    exposedDropDown?.apply {
                        if (categoryList.isNotEmpty() && taskId == NOT_SET) {
                            val initialSelection = adapter.getItem(0).toString()
                            setText(initialSelection, false)
                            viewModel.getCategoryCollections(categoryTitle = initialSelection)
                        }
                        setAdapter(adapter)
                        setOnItemClickListener { parent, view, position, id ->
                            viewModel.getCategoryCollections(categoryTitle = categoryList[position])
                        }
                        *//*onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                viewModel.getCategoryCollections(categoryTitle = categoryList[position])
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                //
                            }

                        }*//*
                    }
                }
            }

            launch {
                viewModel.categoryCollections.collectLatest { collectionList ->
                    val adapter = ArrayAdapter(
                        binding.ilCollection.context,
                        R.layout.list_item_category,
                        collectionList
                    )
                    val exposedDropDown =
                        binding.ilCollection.editText as MaterialAutoCompleteTextView
                    exposedDropDown.apply {
                        if (collectionList.isNotEmpty() && taskId == NOT_SET) {
                            val initialSelection = adapter.getItem(0).toString()
                            setText(initialSelection, false)
                        }
                        setAdapter(adapter)
                    }
                }
            }

        }*/

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*    companion object {
            const val TAG = "ModalBottomSheet"
        }*/

}