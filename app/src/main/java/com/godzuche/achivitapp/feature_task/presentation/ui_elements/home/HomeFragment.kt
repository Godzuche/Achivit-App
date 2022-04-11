package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.util.dp
import com.godzuche.achivitapp.databinding.FragmentHomeBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.TasksUiEvent
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.FilterBottomSheetDialog
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.ModalBottomSheet
import com.godzuche.achivitapp.feature_task.presentation.util.SnackBarActions
import com.godzuche.achivitapp.feature_task.presentation.util.UiEvent
import com.godzuche.achivitapp.feature_task.presentation.util.home_frag_util.*
import com.google.android.material.R.integer
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.MaterialSharedAxis.Z
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment() {
//    private lateinit var adapter: TaskListAdapter

    private lateinit var task: Task
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var touchHelper: ItemTouchHelper

    private val modalBottomSheet = ModalBottomSheet()
    private val filterBottomSheet = FilterBottomSheetDialog()

    private val viewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.d("Home", "OnCreate")

        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
//        Log.d("Home", "OnCreateView")

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
//        Log.d("Home", "OnStart")

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

/*        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.VISIBLE*/


    }

    override fun onResume() {
        super.onResume()

        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)

        fab?.apply {
            icon = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_baseline_add_24,
                activity?.theme)
            if (!this.isExtended) {
                this.extend()
            }
        }

        fab?.setOnClickListener {
            if (!modalBottomSheet.isAdded) {
                modalBottomSheet.show(childFragmentManager,
                    ModalBottomSheet.TAG)
            }
        }

        binding.recyclerViewTasksList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val linearLayoutManager =
                    (binding.recyclerViewTasksList.layoutManager as LinearLayoutManager)
                val adapter = recyclerView.adapter as TaskListAdapter
                if (newState == SCROLL_STATE_IDLE) {
                    if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (fab?.isExtended == false) fab.extend()
                    } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.currentList.size - 1
                    ) {
                        if (fab?.isExtended == false) fab.extend()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 15 && fab?.isExtended == true) fab.shrink()
                else if (dy < -15 && fab?.isExtended == false) fab.extend()
            }
        })


    }

    override fun onPause() {
        super.onPause()

        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)

        fab?.setOnClickListener(null)

        binding.recyclerViewTasksList.clearOnScrollListeners()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Log.d("Home", "OnViewCreated")

        postponeEnterTransition()
        requireView().doOnPreDraw { startPostponedEnterTransition() }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.action_home,
                R.id.action_notifications,
                R.id.action_profile,
                R.id.action_search,
                R.id.task_fragment
            ),
            fallbackOnNavigateUpListener = { requireActivity().onNavigateUp() }
        )
        binding.toolbarMain.inflateMenu(R.menu.menu_home)
        binding.toolbarMain.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    findNavController().navigate(R.id.action_global_action_settings)
                    true
                }
                R.id.action_filter -> {
                    if (!filterBottomSheet.isAdded) {
                        filterBottomSheet.show(
                            childFragmentManager, FilterBottomSheetDialog.TAG
                        )
                    }
                    true
                }
                R.id.action_search -> {
                    exitTransition = MaterialSharedAxis(Z, true).apply {
                        duration =
                            resources.getInteger(integer.material_motion_duration_long_1).toLong()
                    }
                    reenterTransition = MaterialSharedAxis(Z, false).apply {
                        duration =
                            resources.getInteger(integer.material_motion_duration_long_1).toLong()
                    }
                    val action = HomeFragmentDirections.actionGlobalSearchFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        binding.collapsingToolbarMain.setupWithNavController(
            binding.toolbarMain,
            findNavController(),
        )
        binding.toolbarMain.setupWithNavController(findNavController(), appBarConfiguration)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetAction.emit("Add Task")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetTaskId.emit(-1)
            }
        }

        val displayMetrics: DisplayMetrics = resources.displayMetrics
        val height = (displayMetrics.heightPixels / displayMetrics.density).toInt().dp
        val width = (displayMetrics.widthPixels / displayMetrics.density).toInt().dp

        val deleteIcon =
            ResourcesCompat.getDrawable(resources,
                R.drawable.ic_baseline_delete_24,
                null)
        val snoozeIcon =
            ResourcesCompat.getDrawable(resources,
                R.drawable.ic_baseline_snooze_24,
                null)

        val recyclerViewTaskList = binding.recyclerViewTasksList

        val deleteColor = resources.getColor(android.R.color.holo_red_light, null)
        val deleteColorDark = resources.getColor(android.R.color.holo_red_dark, null)
        val snoozeColor = resources.getColor(android.R.color.holo_orange_light, null)
        val snoozeColorDark = resources.getColor(android.R.color.holo_orange_dark, null)

        val adapter = TaskListAdapter { cardView, task ->
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(integer.material_motion_duration_long_1).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(integer.material_motion_duration_long_1).toLong()
            }

            val taskCardDetailTransitionName =
                resources.getString(R.string.task_card_detail_transition_name)
            val extras = FragmentNavigatorExtras(cardView to taskCardDetailTransitionName)
            val action = HomeFragmentDirections.actionGlobalTaskFragment(task.id!!)
            findNavController().navigate(action, extras)
//            task = it
//            viewModel.accept(TasksUiEvent.OnTaskClick(it))
        }

        binding.recyclerViewTasksList.adapter = adapter

        touchHelper = ItemTouchHelper(SwipeDragHelper(
            RvColors(deleteColor, deleteColorDark, snoozeColor, snoozeColorDark),
            Icons(deleteIcon, snoozeIcon),
            Measurements(height, width),
            ViewUtil(adapter,
                binding.coordinator,
                activity?.findViewById(R.id.fab_add),
                requireContext(),
                resources),
            viewModel, viewLifecycleOwner
        ))

        touchHelper.attachToRecyclerView(recyclerViewTaskList)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .map { it.tasksItems }
                    .collectLatest { tasks ->
                        Log.d("Home", "Adapter Submit list size: ${tasks.size}")
                        adapter.submitList(tasks)
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar -> {
                            val snackBar = Snackbar.make(binding.coordinator,
                                event.message,
                                Snackbar.LENGTH_LONG)
                                .setAnchorView(activity?.findViewById(R.id.fab_add))

                            if (event.action == SnackBarActions.UNDO) {
                                snackBar.setAction(event.action) {
                                    viewModel.accept(TasksUiEvent.OnUndoDeleteClick)
                                }.show()
                            }
                        }
                        is UiEvent.ScrollToTop -> {
                            Log.d("Home", "Scroll to top")
                            binding.recyclerViewTasksList.smoothScrollToPosition(0)
                        }
                        is UiEvent.Navigate -> {
                            //
                        }
                        else -> Unit
                    }
                }
            }
        }


        val chipGroup = activity?.findViewById<ChipGroup>(R.id.chip_group)
        when (chipGroup?.checkedChipId) {
            R.id.chip_my_tasks -> {
//                getTaskCollections()
            }
        }

        chipGroup?.setOnCheckedChangeListener { group, checkedId ->
            //
        }

    }

    override fun onStop() {
        super.onStop()
//        Log.d("Home", "OnStop")
    }

    override fun onDestroy() {
        super.onDestroy()
//        Log.d("Home", "OnDestroy")
        _binding = null
    }

}