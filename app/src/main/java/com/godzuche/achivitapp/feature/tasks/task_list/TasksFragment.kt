package com.godzuche.achivitapp.feature.tasks.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.core.design_system.theme.AchivitTheme
import com.godzuche.achivitapp.databinding.FragmentTasksBinding
import com.godzuche.achivitapp.feature.tasks.util.DialogTitle
import com.godzuche.achivitapp.feature.tasks.util.SnackBarActions
import com.godzuche.achivitapp.feature.tasks.util.UiEvent
import com.google.android.material.R.integer
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var touchHelper: ItemTouchHelper
    private val tasksViewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1)
                    .toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1)
                    .toLong()
        }
        reenterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)
        _binding = FragmentTasksBinding.bind(view)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val addTaskFab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        addTaskFab?.apply {
            setIconResource(R.drawable.ic_baseline_add_24)
            if (!this.isExtended) {
                this.postDelayed({ extend() }, 150)
            }
        }

        addTaskFab?.setOnClickListener {
            findNavController().navigate(TasksFragmentDirections.actionGlobalModalBottomSheet(taskId = NOT_SET))
        }

        /*binding.recyclerViewTasksList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                doOnScrollChanged(recyclerView, newState, binding)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                doOnScrolled(dy)
            }
        })*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        requireView().doOnPreDraw {
            startPostponedEnterTransition()
        }

        /* binding.toolbarMain.apply {
             inflateMenu(R.menu.menu_home)
             setOnMenuItemClickListener { item ->
                 when (item.itemId) {
                     R.id.action_settings -> {
                         exitTransition = MaterialSharedAxis(Z, true).apply {
                             duration =
                                 resources.getInteger(integer.material_motion_duration_medium_1)
                                     .toLong()
                         }
                         reenterTransition = MaterialSharedAxis(Z, false).apply {
                             duration =
                                 resources.getInteger(integer.material_motion_duration_medium_1)
                                     .toLong()
                         }
                         val directions = TasksFragmentDirections.actionGlobalSettingsFragment()
                         findNavController().navigate(directions)
                         true
                     }

                     R.id.action_filter -> {
                         val directions =
                             TasksFragmentDirections.actionGlobalFilterBottomSheetDialog()
                         findNavController().navigate(directions)
                         true
                     }

                     R.id.action_search -> {
                         exitTransition = MaterialSharedAxis(Z, true).apply {
                             duration =
                                 resources.getInteger(integer.material_motion_duration_medium_1)
                                     .toLong()
                         }
                         reenterTransition = MaterialSharedAxis(Z, false).apply {
                             duration =
                                 resources.getInteger(integer.material_motion_duration_medium_1)
                                     .toLong()
                         }
                         val action = TasksFragmentDirections.actionGlobalSearchFragment()
                         findNavController().navigate(action)
                         true
                     }

                     else -> false
                 }
             }
         }*/

        binding.composeTopBar.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AchivitTheme {
                    val dialogState by tasksViewModel.dialogState.collectAsStateWithLifecycle()
                    if (dialogState.shouldShow) {
                        dialogState.dialog?.let { dialog ->
                            AchivitDialog(
                                achivitDialog = dialog,
                                onDismiss = { tasksViewModel.setDialogState(shouldShow = false) },
                                onDismissRequest = { tasksViewModel.setDialogState(shouldShow = false) },
                                onConfirm = {
                                    when (dialog) {
                                        is ConfirmationDialog -> {
                                            when (val action = dialog.action) {
                                                is ConfirmActions.DeleteTask -> {
                                                    tasksViewModel.setDialogState(shouldShow = false)
                                                    tasksViewModel.accept(
                                                        TasksUiEvent.OnDeleteConfirm(
                                                            task = action.task
                                                        )
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

                    TasksRoute(
                        tasksViewModel = tasksViewModel,
                        onNavigateToTaskDetail = { taskId ->
                            exitTransition = MaterialElevationScale(false).apply {
                                duration =
                                    resources.getInteger(integer.material_motion_duration_medium_1)
                                        .toLong()
                            }
                            reenterTransition = MaterialElevationScale(true).apply {
                                duration =
                                    resources.getInteger(integer.material_motion_duration_medium_1)
                                        .toLong()
                            }
                            // Todo: Add a SharedElement transition animation.
                            val action =
                                TasksFragmentDirections.actionGlobalTaskFragment(taskId)
                            findNavController().navigate(action)
                        },
                        onAddNewTaskCategory = {
                            val action =
                                TasksFragmentDirections.actionGlobalAddCategoryCollectionFragment(
                                    DialogTitle.CATEGORY
                                )
                            findNavController().navigate(action)
                        },
                        onTopBarAction = { topBarAction ->
                            exitTransition = MaterialSharedAxis(Z, true).apply {
                                duration =
                                    resources.getInteger(integer.material_motion_duration_medium_1)
                                        .toLong()
                            }
                            reenterTransition = MaterialSharedAxis(Z, false).apply {
                                duration =
                                    resources.getInteger(integer.material_motion_duration_medium_1)
                                        .toLong()
                            }
                            when (topBarAction) {
                                TasksTopBarActions.SEARCH -> {
                                    val action =
                                        TasksFragmentDirections.actionGlobalSearchFragment()
                                    findNavController().navigate(action)
                                }

                                TasksTopBarActions.SETTINGS -> {
                                    val action =
                                        TasksFragmentDirections.actionGlobalSettingsFragment()
                                    findNavController().navigate(action)
                                }

                                TasksTopBarActions.FILTER -> {
                                    val action =
                                        TasksFragmentDirections.actionGlobalFilterBottomSheetDialog()
                                    findNavController().navigate(action)
                                }
                            }
                        }
                    )
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { tasksViewModel.bottomSheetAction.emit("Add Task") }
                launch { tasksViewModel.bottomSheetTaskId.emit(-1) }
            }
        }

        /*        val displayMetrics: DisplayMetrics = resources.displayMetrics
                val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().toDp
                val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().toDp*/

        /*        val deleteIcon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_delete_24,
                        null
                    )
                val snoozeIcon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_snooze_24,
                        null
                    )*/

//        val recyclerViewTaskList = binding.recyclerViewTasksList

        /*        val deleteColor = resources.getColor(android.R.color.holo_red_light, null)
                val deleteColorDark = resources.getColor(android.R.color.holo_red_dark, null)
                val snoozeColor = resources.getColor(android.R.color.holo_orange_light, null)
                val snoozeColorDark = resources.getColor(android.R.color.holo_orange_dark, null)*/

        /*        val adapter = TaskListAdapter(
                    onItemClicked = { cardView, task ->
                        exitTransition = MaterialElevationScale(false).apply {
                            duration =
                                resources.getInteger(integer.material_motion_duration_medium_1).toLong()
                        }
                        reenterTransition = MaterialElevationScale(true).apply {
                            duration =
                                resources.getInteger(integer.material_motion_duration_medium_1).toLong()
                        }

                        val taskCardDetailTransitionName =
                            resources.getString(R.string.task_card_detail_transition_name)
                        val extras = FragmentNavigatorExtras(cardView to taskCardDetailTransitionName)
                        val action = TasksFragmentDirections.actionGlobalTaskFragment(task.id!!)
                        findNavController().navigate(action, extras)
        //            task = it
        //            viewModel.accept(TasksUiEvent.OnTaskClick(it))
                    }
                ) { task, isChecked ->
                    tasksViewModel.setIsCompleted(task, isChecked)
                }*/

//        binding.recyclerViewTasksList.adapter = adapter

        /*touchHelper = ItemTouchHelper(
            SwipeDragHelper(
                RvColors(deleteColor, deleteColorDark, snoozeColor, snoozeColorDark),
                Icons(deleteIcon, snoozeIcon),
                Measurements(height, width),
                ViewUtil(
                    binding.recyclerViewTasksList.layoutManager as LinearLayoutManager,
                    adapter,
                    binding.coordinator,
                    activity?.findViewById(R.id.fab_add),
                    requireContext(),
                    resources
                ),
                viewModel, viewLifecycleOwner
            )
        )*/

//        touchHelper.attachToRecyclerView(recyclerViewTaskList)

        /*        viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        tasksViewModel.tasksPagingDataFlow.collect {
                            adapter.submitData(it)
                        }
                    }
                }*/

        // Todo: Hoist this to MainActivity or AchivitApp
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasksViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar -> {
                            val snackBar = Snackbar.make(
                                binding.coordinator,
                                event.message,
                                Snackbar.LENGTH_LONG
                            )
                                .setAnchorView(activity?.findViewById(R.id.fab_add))

                            if (event.action == SnackBarActions.UNDO) {
                                snackBar.setAction(event.action) {
                                    tasksViewModel.accept(TasksUiEvent.OnUndoDeleteClick)
                                }.show()
                            }
                        }

                        is UiEvent.ScrollToTop -> {
//                            binding.recyclerViewTasksList.smoothScrollToPosition(0)
                        }

                        is UiEvent.ScrollToBottom -> {
//                            binding.recyclerViewTasksList.smoothScrollToPosition(adapter.snapshot().items.size - 1)
                        }

                        is UiEvent.Navigate -> {
                            // Todo: Refactor because route is for compose navigation
                        }

                        else -> Unit
                    }
                }
            }
        }

        /*        viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            tasksViewModel.categories.collectLatest { categoryList ->
                                val chipGroup = binding.chipGroup
                                chipGroup.removeAllViews()
                                categoryList.forEachIndexed { index, title ->
                                    val chip = layoutInflater.inflate(
                                        R.layout.single_chip_layout,
                                        chipGroup,
                                        false
                                    ) as Chip
                                    chip.text = title
                                    chip.id = index
                                    chipGroup.addView(chip)
                                    if (index == (categoryList.size - 1)) {
                                        launch {
                                            viewModel.uiState
                                                .distinctUntilChangedBy { it.checkedCategoryFilterChipId }
                                                .collectLatest { state ->
                                                    val checkedId = state.checkedCategoryFilterChipId
                                                    binding.chipGroup.clearCheck()
                                                    binding.chipGroup.check(checkedId)
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }*/

        /*binding.chipAddCategory.setOnClickListener {
            val directions = TasksFragmentDirections.actionGlobalAddCategoryCollectionFragment(
                DialogTitle.CATEGORY
            )
            findNavController().navigate(directions)
        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedId = checkedIds.first()
            val checkedTitle = (group[checkedId] as Chip).text.toString()
            Timber.tag("TasksChip").d("Checked change id: $checkedId text: $checkedTitle")
            viewModel.setCheckedCategoryChip(checkedId, checkedTitle)
            viewModel.setCheckedCollectionChip(NOT_SET, "")
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        fab?.setOnClickListener(null)
//        binding.recyclerViewTasksList.clearOnScrollListeners()
    }


    companion object {
        const val NOT_SET = -1
    }
}