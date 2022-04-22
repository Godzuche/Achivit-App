package com.godzuche.achivitapp.feature_task.presentation.util.task_frag_util

import android.content.res.Resources
import androidx.fragment.app.Fragment
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTaskBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.task_details.TaskDetailViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

object DateTimePickerUtil {

    private var formattedDateString = ""
    private var mHour = 0
    private var mMinute = 0
    private var sHour: Int = 0
    private var dateSelection: Long = 0L

    fun millisToString(timeMillis: Long): String {
        val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
        return formatter.format(timeMillis)
    }

    fun to12HrsFormat(hours: Int): Int {
        return when {
            hours == 12 -> {
                hours
            }
            hours > 12 -> {
                hours - 12
            }
            else -> {
                hours
            }
        }
    }

    fun getTimeSuffix(hours: Int): String {
        val timeSuffix = when {
            hours == 12 -> {
                "PM"
            }
            hours > 12 -> {
                "PM"
            }
            else -> {
                "AM"
            }
        }
        return timeSuffix
    }

    @ExperimentalCoroutinesApi
    fun Fragment.setupOnChipClickDateTimePicker(
        task: Task,
        binding: FragmentTaskBinding,
        viewModel: TaskDetailViewModel,
    ) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(task.hours)
            .setMinute(task.minutes)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Select time")
            .build()

        val materialDatePicker = MaterialDatePicker.Builder.datePicker()

        val datePicker = materialDatePicker
            .setTitleText("Select date")
            .setSelection(task.date)
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

                mMinute = this.minute
                sHour = this.hour

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

                binding.chipTime.apply {
                    text = getString(R.string.date_time, formattedDateString,
                        mHour,
                        mMinute,
                        timeSuffix)
                }

                task.apply {
                    this.id?.let { id ->
                        viewModel.updateTask(
                            id,
                            this.title,
                            this.description,
                            dateSelection,
                            sHour,
                            mMinute
                        )
                    }
                }

            }
        }

        datePicker.apply {
            addOnPositiveButtonClickListener {
                dateSelection = this.selection!!
                val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
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

        binding.chipTime.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 -> datePicker.show(it1, "date_picker_tag") }
        }

    }

    fun formatTaskDateTime(task: Task): String {
        val timeSuffix: String
        val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
        val dateSelection = task.date
        val formattedDateString = formatter.format(dateSelection)
        val mHour = when {
            task.hours == 12 -> {
                timeSuffix = "PM"
                task.hours
            }
            task.hours > 12 -> {
                timeSuffix = "PM"
                task.hours - 12
            }
            else -> {
                timeSuffix = "AM"
                task.hours
            }
        }

        return Resources.getSystem().getString(R.string.date_time,
            formattedDateString,
            mHour,
            task.minutes,
            timeSuffix)

    }

    fun BottomSheetDialogFragment.formatDateTime(
        minutes: Int,
        hours: Int,
        dateSelection: Long,
    ): String {
        val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
        val formattedDateString = formatter.format(dateSelection)
        val timeSuffix: String
        val mHour = when {
            hours == 12 -> {
                timeSuffix = "PM"
                hours
            }
            hours > 12 -> {
                timeSuffix = "PM"
                hours - 12
            }
            else -> {
                timeSuffix = "AM"
                hours
            }
        }

        return resources.getString(R.string.date_time,
            formattedDateString,
            mHour,
            minutes,
            timeSuffix)

    }

}