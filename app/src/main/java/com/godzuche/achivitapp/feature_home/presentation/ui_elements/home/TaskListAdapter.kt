package com.godzuche.achivitapp.feature_home.presentation.ui_elements.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ItemTaskListBinding
import com.godzuche.achivitapp.feature_home.domain.model.Task
import com.godzuche.achivitapp.feature_home.presentation.util.TaskStatus
import com.godzuche.achivitapp.feature_home.presentation.util.task_frag_util.DateTimePickerUtil.convertMillisToString
import com.google.android.material.composethemeadapter3.Mdc3Theme
import timber.log.Timber

class TaskListAdapter(
    private val onItemClicked: (View, Task) -> Unit,
    private val onDoneCheck: (Task, Boolean) -> Unit
) : PagingDataAdapter<Task, TaskListAdapter.TaskViewHolder>(TASK_COMPARATOR) {

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
                    itemView.resources.getString(
                        R.string.task_card_transition_name,
                        task.id.toString()
                    )

                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description

                val taskDueDate = task.dueDate
                chipTimeDate.text = convertMillisToString(taskDueDate)

                taskColorView.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        Mdc3Theme {
                            val statusColor by remember(task.status) {
                                mutableStateOf(
                                    when (task.status) {
                                        TaskStatus.IN_PROGRESS -> Color(0xFFFFA500)
                                        TaskStatus.COMPLETED -> Color(0xFF52D726)
                                        else -> Color.Gray
                                    }
                                )
                            }
                            TaskStatusColor(color = statusColor)
                        }
                    }
                }
                checkBox.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        Mdc3Theme {
                            val isCheckedStatus by remember(task.isCompleted) {
                                mutableStateOf(task.isCompleted)
                            }
                            DoneCheckBox(
                                checked = isCheckedStatus,
                                onCheckChanged = { isChecked ->
                                    Timber.i("CheckChange", isChecked)
                                    onDoneCheck(task, isChecked)
                                }
                            )
                        }
                    }
                }
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
