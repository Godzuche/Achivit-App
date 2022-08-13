package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ItemTaskListBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import com.godzuche.achivitapp.feature_task.presentation.util.task_frag_util.DateTimePickerUtil.convertMillisToString

class TaskListAdapter(private val onItemClicked: (View, Task) -> Unit) :
    PagingDataAdapter<Task, TaskListAdapter.TaskViewHolder>(TASK_COMPARATOR) {

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
        if (currentTask != null) {
            holder.bind(currentTask)
        }
    }

    inner class TaskViewHolder(private val binding: ItemTaskListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var currentItem: Task? = null

        init {
            itemView.setOnClickListener {
                binding.apply {
                    root.apply {
                        if (this.isChecked) {
                            this.isChecked = false
                        } else {
                            currentItem?.let { task ->
                                onItemClicked(itemCardView, task)
                            }
                        }
                    }
                }
            }

        }

        fun bind(task: Task) {
            binding.apply {
                itemCardView.transitionName =
                    itemView.resources.getString(R.string.task_card_transition_name,
                        task.id.toString())

                //TODO: Let the user check the CheckBox instead
                checkBox.isChecked = (task.status == TaskStatus.IN_PROGRESS)

                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description

                val taskDueDate = task.dueDate
                chipTimeDate.text = convertMillisToString(taskDueDate)
            }
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
