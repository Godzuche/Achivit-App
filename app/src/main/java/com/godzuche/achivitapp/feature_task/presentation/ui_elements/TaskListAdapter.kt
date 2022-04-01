package com.godzuche.achivitapp.feature_task.presentation.ui_elements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ItemTaskListBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskListAdapter(private val onItemClicked: (Task) -> Unit) :
    ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TASK_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.currentItem = currentTask
        holder.bind(currentTask)
    }

    inner class TaskViewHolder(private val binding: ItemTaskListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var timeSuffix: String
        var currentItem: Task? = null

        init {
            itemView.setOnClickListener {
                binding.root.apply {
                    if (this.isChecked) {
                        this.isChecked = false
                    } else {
                        currentItem?.let { task -> onItemClicked(task) }
                    }
                }
            }

            /*binding.root.setOnLongClickListener {
                binding.root.isChecked = !binding.root.isChecked
                true
            }*/
        }

        fun bind(task: Task) {
            binding.tvTaskTitle.text = task.title
            binding.tvTaskDescription.text = task.description

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

            binding.chipTimeDate.text = itemView.resources.getString(
                R.string.date_time, formattedDateString, mHour,
                task.minutes,
                timeSuffix)
        }

    }

    companion object {
        private val TASK_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {

            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }

        }
    }
}
