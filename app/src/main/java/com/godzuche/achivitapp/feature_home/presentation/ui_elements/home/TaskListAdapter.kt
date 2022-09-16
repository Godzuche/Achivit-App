package com.godzuche.achivitapp.feature_home.presentation.ui_elements.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ItemTaskListBinding
import com.godzuche.achivitapp.feature_home.domain.model.Task
import com.godzuche.achivitapp.feature_home.presentation.util.task_frag_util.DateTimePickerUtil.convertMillisToString
import com.google.android.material.composethemeadapter3.Mdc3Theme

@Composable
fun TaskColor(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color = Color.Cyan)
            .then(modifier)
    )
}

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
                    itemView.resources.getString(
                        R.string.task_card_transition_name,
                        task.id.toString()
                    )

                //TODO: Let the user check the CheckBox instead
//                checkBox.isChecked = (task.status == TaskStatus.IN_PROGRESS)

                tvTaskTitle.text = task.title
                tvTaskDescription.text = task.description

                val taskDueDate = task.dueDate
                chipTimeDate.text = convertMillisToString(taskDueDate)

                taskColorView.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        Mdc3Theme {
                            TaskColor()
                        }
                    }
                }
                checkBox.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        Mdc3Theme {
                            var isChecked by remember() {
                                mutableStateOf(false)
                            }
                            DoneCheckBox(
                                checked = isChecked,
                                onCheckChanged = { isChecked = !isChecked }
                            )
                        }
                    }
                }
            }
        }

    }

    @Composable
    fun DoneCheckBox(
        modifier: Modifier = Modifier,
        checked: Boolean,
        onCheckChanged: (Boolean) -> Unit
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChanged,
            modifier = Modifier
//                .size(48.dp)
                .then(modifier),
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF99CC00),
                uncheckedColor = Color.White,
                checkmarkColor = Color.White
            )
        )
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
