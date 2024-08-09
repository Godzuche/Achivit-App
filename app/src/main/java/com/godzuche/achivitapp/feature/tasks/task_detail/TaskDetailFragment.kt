package com.godzuche.achivitapp.feature.tasks.task_detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.databinding.FragmentTaskDetailBinding
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmActions
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmationDialog
import com.godzuche.achivitapp.feature.tasks.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.R.integer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskDetailFragment : Fragment() {
    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    private val tasksViewModel: TasksViewModel by activityViewModels()
    private val taskDetailViewModel: TaskDetailViewModel by viewModels()

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
//            setAllContainerColors(requireContext().themeColor(attr.colorSurface))
        }
//        viewModel.taskId.value = navigationArgs.id.toLong()
        val taskId = navigationArgs.id
        taskDetailViewModel.accept(TaskUiEvent.OnRetrieveTask(taskId = taskId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        /*_binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root*/

        return ComposeView(requireContext()).apply {
            id = R.id.task_detail_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isTransitionGroup = true

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Mdc3Theme(setDefaultFontFamily = true) {
                    val dialogState by taskDetailViewModel.dialogState.collectAsStateWithLifecycle()
                    if (dialogState.shouldShow) {
                        dialogState.dialog?.let { dialog ->
                            AchivitDialog(
                                achivitDialog = dialog,
                                onDismiss = { taskDetailViewModel.setDialogState(shouldShow = false) },
                                onDismissRequest = { taskDetailViewModel.setDialogState(shouldShow = false) },
                                onConfirm = {
                                    when (dialog) {
                                        is ConfirmationDialog -> {
                                            when (val action = dialog.action) {
                                                is ConfirmActions.DeleteTask -> {
                                                    taskDetailViewModel.setDialogState(shouldShow = false)
                                                    findNavController().popBackStack()

                                                    tasksViewModel.accept(
                                                        TasksUiEvent.OnDeleteConfirm(task = action.task)
                                                    )
                                                }

                                                else -> Unit
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    TaskDetailRoute(
                        onDeleteTask = {
                            taskDetailViewModel.accept(TaskUiEvent.OnDeleteTask(it))
                        },
                        onNavigateBack = {
                            findNavController().popBackStack()
                        }
                    )
                }
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*    postponeEnterTransition()
            requireView().doOnPreDraw { startPostponedEnterTransition() }*/

        /* binding.toolbar.apply {
             inflateMenu(R.menu.menu_edit_task)
             setNavigationOnClickListener { findNavController().navigateUp() }
             setOnMenuItemClickListener { item ->
                 if (item.itemId == R.id.action_delete_task) {
                     showConfirmationDialog()
                     true
                 } else false
             }
         }

         viewLifecycleOwner.lifecycleScope.launch {
             viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                 launch {
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
         }*/

    }

    /*    private fun bind(task: Task) {
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
        }*/

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
            taskDetailViewModel.accept(
                TaskUiEvent.OnDeleteTask(
                    task = it
                )
            )
            tasksViewModel.accept(TasksUiEvent.OnDeleteFromTaskDetail(deletedTask = it))
        }
    }

}