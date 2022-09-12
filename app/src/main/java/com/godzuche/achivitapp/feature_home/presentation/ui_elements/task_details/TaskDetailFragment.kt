package com.godzuche.achivitapp.feature_home.presentation.ui_elements.task_details

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTaskBinding
import com.godzuche.achivitapp.feature_home.domain.model.Task
import com.godzuche.achivitapp.feature_home.presentation.state_holder.TaskDetailViewModel
import com.godzuche.achivitapp.feature_home.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_home.presentation.ui_elements.home.TasksUiEvent
import com.godzuche.achivitapp.feature_home.presentation.ui_state.TasksUiState
import com.godzuche.achivitapp.feature_home.presentation.util.SnackBarActions
import com.godzuche.achivitapp.feature_home.presentation.util.UiEvent
import com.godzuche.achivitapp.feature_home.presentation.util.task_frag_util.DateTimePickerUtil.convertMillisToString
import com.godzuche.achivitapp.feature_home.presentation.util.task_frag_util.DateTimePickerUtil.setupOnChipClickDateTimePicker
import com.godzuche.achivitapp.feature_home.presentation.util.themeColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val activityViewModels: TasksViewModel by activityViewModels()
    private val viewModel: TaskDetailViewModel by viewModels()

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.taskId.value = navigationArgs.id.toLong()
        val taskId = navigationArgs.id
        viewModel.accept(TaskUiEvent.OnRetrieveTask(taskId = taskId))

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(com.google.android.material.R.attr.colorSurface))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        requireView().doOnPreDraw { startPostponedEnterTransition() }

        binding.toolbar.apply {
            inflateMenu(R.menu.menu_edit_task)
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_delete_task) {
                    showConfirmationDialog()
                    true
                } else false
            }
        }

//        val id = navigationArgs.id

        /*       binding.bindState(
                   task = clickedTask,
                   uiState = viewModel.uiState,
                   uiActions = viewModel.accept
               )
       */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { /*viewModel.retrieveTask(id).collect { clickedTask ->
                    task = clickedTask
//                    bind(task!!)
                    setupDateTimePicker(clickedTask, binding, viewModel)
                }*/
                    viewModel.detail
                        .collectLatest {
                            if (it != null) {
                                task = it
                                bind(it)
                                setupOnChipClickDateTimePicker(it, binding, viewModel)
                            }
                        }
                }
                launch {
                    viewModel.uiEvent.collectLatest { event ->
                        when (event) {
                            is UiEvent.ShowSnackBar -> {
                                val snackBar =
                                    Snackbar.make(
                                        binding.coordinator,
                                        event.message,
                                        Snackbar.LENGTH_LONG
                                    )
                                        .setAnchorView(activity?.findViewById(R.id.fab_add))

                                if (event.action == SnackBarActions.UNDO) {
                                    snackBar.setAction(event.action) {
                                        viewModel.accept(TaskUiEvent.OnUndoDeleteClick)
                                    }.show()
                                }
                            }
                            is UiEvent.PopBackStack -> {
                                findNavController().popBackStack()
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }

    }

    private fun FragmentTaskBinding.bindState(
        task: Task,
        uiState: StateFlow<TasksUiState>,
//        pagingData: Flow<PagingData<Task>>,
        uiActions: (TasksUiEvent) -> Unit,
    ) {

        bindDetails(uiState = uiState)
        onUiEvents(uiActions = uiActions)
    }

    private fun onUiEvents(uiActions: (TasksUiEvent) -> Unit) {
        //
    }

    private fun FragmentTaskBinding.bindDetails(uiState: StateFlow<TasksUiState>) {
        //
    }

    private fun bind(task: Task) {
        binding.apply {
            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description

            val taskDueDate = task.dueDate
            chipTime.apply {
                text = convertMillisToString(taskDueDate)
                visibility = View.VISIBLE
            }

            val createdDate = task.created
            tvCreatedDate.text =
                createdDate?.let { getString(R.string.created, convertMillisToString(createdDate)) }
        }
    }

    override fun onResume() {
        super.onResume()

        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)

        fab?.apply {
            icon = ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_edit_24,
                activity?.theme
            )
            if (this.isExtended) {
                this.shrink()
            }
        }

        fab?.setOnClickListener {
            findNavController().navigate(
                TaskDetailFragmentDirections.actionGlobalModalBottomSheet(
                    navigationArgs.id
                )
            )

        }

    }

    override fun onPause() {
        super.onPause()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener(null)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_task))
            .setMessage(getString(R.string.task_delete_confirmation))
            .setCancelable(false)
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Delete") { _, _ -> deleteTask() }
            .show()
    }

    private fun deleteTask() {
        task?.let {
            viewModel.accept(
                TaskUiEvent.OnDeleteTask(
                    task = it
                )
            )
            activityViewModels.accept(TasksUiEvent.OnDeleteFromTaskDetail(deletedTask = it))
        }
    }

}