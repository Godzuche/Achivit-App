package com.godzuche.achivitapp.feature_tasks_feed.task_list

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTasksBinding
import com.godzuche.achivitapp.feature_home.presentation.core.util.dp
import com.godzuche.achivitapp.feature_tasks_feed.task_list.util.*
import com.godzuche.achivitapp.feature_tasks_feed.task_list.util.ExtFunctions.doOnScrollChanged
import com.godzuche.achivitapp.feature_tasks_feed.task_list.util.ExtFunctions.doOnScrolled
import com.godzuche.achivitapp.feature_tasks_feed.util.DialogTitle
import com.godzuche.achivitapp.feature_tasks_feed.util.SnackBarActions
import com.godzuche.achivitapp.feature_tasks_feed.util.UiEvent
import com.google.android.material.R.integer
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var touchHelper: ItemTouchHelper
    private val viewModel: TasksViewModel by activityViewModels()

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

        binding.recyclerViewTasksList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                doOnScrollChanged(recyclerView, newState, binding)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                doOnScrolled(dy)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        requireView().doOnPreDraw {
            startPostponedEnterTransition()
        }

        binding.toolbarMain.apply {
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
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.bottomSheetAction.emit("Add Task") }
                launch { viewModel.bottomSheetTaskId.emit(-1) }
            }
        }

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().dp
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon =
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
            )

        val recyclerViewTaskList = binding.recyclerViewTasksList

        val deleteColor = resources.getColor(android.R.color.holo_red_light, null)
        val deleteColorDark = resources.getColor(android.R.color.holo_red_dark, null)
        val snoozeColor = resources.getColor(android.R.color.holo_orange_light, null)
        val snoozeColorDark = resources.getColor(android.R.color.holo_orange_dark, null)

        val adapter = TaskListAdapter(
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
            viewModel.setIsCompleted(task, isChecked)
        }

        binding.recyclerViewTasksList.adapter = adapter

        touchHelper = ItemTouchHelper(
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
        )

        touchHelper.attachToRecyclerView(recyclerViewTaskList)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasksPagingDataFlow.collect {
                    adapter.submitData(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
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
                                    viewModel.accept(TasksUiEvent.OnUndoDeleteClick)
                                }.show()
                            }
                        }
                        is UiEvent.ScrollToTop -> {
                            binding.recyclerViewTasksList.smoothScrollToPosition(0)
                        }
                        is UiEvent.ScrollToBottom -> {
                            binding.recyclerViewTasksList.smoothScrollToPosition(adapter.snapshot().items.size - 1)
                        }
                        is UiEvent.Navigate -> {
                            //
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collectLatest { categoryList ->
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
        }

        binding.chipAddCategory.setOnClickListener {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        fab?.setOnClickListener(null)
        binding.recyclerViewTasksList.clearOnScrollListeners()
    }


    companion object {
        const val NOT_SET = -1
    }
}