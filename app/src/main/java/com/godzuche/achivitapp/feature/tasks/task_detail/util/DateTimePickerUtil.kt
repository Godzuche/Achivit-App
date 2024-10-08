package com.godzuche.achivitapp.feature.tasks.task_detail.util
/*

import androidx.fragment.app.Fragment
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.databinding.FragmentTaskDetailBinding
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.task_detail.TaskDetailViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Calendar
import java.util.Date

object DateTimePickerUtil {

    private var mMinute = 0
    private var mHour = 0
    private var mYear = 0
    private var mMonth = 0
    private var mDayOfMonth = 0
    private var dateSelection: Long = 0L

    private val mCalendar = Calendar.getInstance()

    @ExperimentalCoroutinesApi
    fun Fragment.setupOnChipClickDateTimePicker(
        task: Task,
        binding: FragmentTaskDetailBinding,
        viewModel: TaskDetailViewModel,
    ) {
        val cal = Calendar.getInstance()
        cal.time = Date(task.dueDate)
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]
        val hour = cal[Calendar.MINUTE]
        val minute = cal[Calendar.MINUTE]
//        val second = cal[Calendar.SECOND]
//        val millis = cal[Calendar.MILLISECOND]

        */
/*       val dueDate = Calendar.getInstance().apply {
                   set(Calendar.YEAR, year)
                   set(Calendar.MONTH, month)
                   set(Calendar.DAY_OF_MONTH, day)
                   set(Calendar.MINUTE, mMinute)
                   set(Calendar.HOUR_OF_DAY, sHour)
                   set(Calendar.SECOND, 0)
                   set(Calendar.MILLISECOND, 0)
               }.timeInMillis*//*


        // bind time
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTitleText("Select time")
            .build()

        val materialDatePicker = MaterialDatePicker.Builder.datePicker()

        val taskDateSelection = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }.timeInMillis

        // bind date
        val datePicker = materialDatePicker
            .setTitleText("Select date")
            .setSelection(taskDateSelection)
            .build()

        timePicker.apply {
            addOnCancelListener {
                activity?.supportFragmentManager?.let { it1 ->
                    datePicker.show(
                        it1,
                        "date_picker_tag"
                    )
                }
            }
//            isCancelable = false
            addOnNegativeButtonClickListener {
                activity?.supportFragmentManager?.let { it1 ->
                    datePicker.show(
                        it1,
                        "date_picker_tag"
                    )
                }
            }

            addOnPositiveButtonClickListener {
                mMinute = this.minute
                mHour = this.hour
                val taskDueDate = mCalendar.apply {
                    set(Calendar.YEAR, mYear)
                    set(Calendar.MONTH, mMonth)
                    set(Calendar.DAY_OF_MONTH, mDayOfMonth)
                    set(Calendar.HOUR_OF_DAY, mHour)
                    set(Calendar.MINUTE, mMinute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                binding.chipTime.apply {
                    text = taskDueDate.millisToString()
                }

                task.apply {
                    this.id?.let { id ->
                        viewModel.updateTask(
                            id,
                            this.title,
                            this.description,
                            taskDueDate,
                            this.collectionTitle,
                            this.categoryTitle,
                        )
                    }
                }

            }
        }

        datePicker.apply {
            addOnPositiveButtonClickListener {
                dateSelection = this.selection!!
                val calend = Calendar.getInstance()
                calend.timeInMillis = dateSelection
                mYear = calend[Calendar.YEAR]
                mMonth = calend[Calendar.MONTH]
                mDayOfMonth = calend[Calendar.DAY_OF_MONTH]

                activity?.supportFragmentManager?.let { it1 ->
                    timePicker.show(
                        it1,
                        "time_picker_tag"
                    )
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

}*/
