package com.godzuche.achivitapp.feature.feed.task_detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTaskDetailBinding
import com.godzuche.achivitapp.domain.model.Task
import com.godzuche.achivitapp.feature.feed.task_detail.util.DateTimePickerUtil.setupOnChipClickDateTimePicker
import com.godzuche.achivitapp.feature.feed.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.feed.task_list.TasksViewModel
import com.godzuche.achivitapp.feature.feed.ui_state.TasksUiState
import com.godzuche.achivitapp.feature.feed.util.SnackBarActions
import com.godzuche.achivitapp.feature.feed.util.UiEvent
import com.godzuche.achivitapp.feature.feed.util.themeColor
import com.godzuche.achivitapp.feature.home.presentation.millisToString
import com.google.android.material.R.attr
import com.google.android.material.R.integer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val activityViewModels: TasksViewModel by activityViewModels()
    private val viewModel: TaskDetailViewModel by viewModels()

    private val navigationArgs: TaskDetailFragmentArgs by navArgs()

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration =
                resources.getInteger(integer.material_motion_duration_medium_1)
                    .toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(attr.colorSurface))
        }
//        viewModel.taskId.value = navigationArgs.id.toLong()
        val taskId = navigationArgs.id
        viewModel.accept(TaskUiEvent.OnRetrieveTask(taskId = taskId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*    postponeEnterTransition()
            requireView().doOnPreDraw { startPostponedEnterTransition() }*/

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
                                Timber.tag("wwww").d("Task created detail frag1: ${task?.created}")
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

    private fun FragmentTaskDetailBinding.bindState(
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

    private fun FragmentTaskDetailBinding.bindDetails(uiState: StateFlow<TasksUiState>) {
        //
    }

    private fun bind(task: Task) {
        binding.apply {
            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description

            val taskDueDate = task.dueDate
            chipTime.apply {
                text = taskDueDate.millisToString()
                visibility = View.VISIBLE
            }

            val createdDate = task.created

            Timber.tag("wwww").d("Task created detail frag2: $createdDate")

            tvCreatedDate.text =
                createdDate?.let { getString(R.string.created, createdDate.millisToString()) }
        }
    }

    override fun onResume() {
        super.onResume()

        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)

        fab?.apply {
            setIconResource(R.drawable.ic_baseline_edit_24)
            if (this.isExtended) {
                this.shrink()
            }
            setOnClickListener {
                findNavController().navigate(
                    TaskDetailFragmentDirections.actionGlobalModalBottomSheet(
                        navigationArgs.id
                    )
                )

            }
        }

//        fab?.setOnClickListener {
//            findNavController().navigate(
//                TaskDetailFragmentDirections.actionGlobalModalBottomSheet(
//                    navigationArgs.id
//                )
//            )
//
//        }

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