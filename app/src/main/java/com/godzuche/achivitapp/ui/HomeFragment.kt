package com.godzuche.achivitapp.ui

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.TaskApplication
import com.godzuche.achivitapp.core.util.dp
import com.godzuche.achivitapp.data.model.Task
import com.godzuche.achivitapp.databinding.FragmentHomeBinding
import com.godzuche.achivitapp.ui.home_frag_util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var adapter: TaskListAdapter
    private lateinit var task: Task
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var swipeHelper: ItemTouchHelper

    private val modalBottomSheet = ModalBottomSheet()

    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication)
                .database.taskDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.bind(view)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.VISIBLE
        activity?.findViewById<Chip>(R.id.chip_add_collection)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.VISIBLE

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_add_24, activity?.theme)

    }

    override fun onResume() {
        super.onResume()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.apply {
                if (!this.isExtended) {
                    this.extend()
                }
            }

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener {

                activity?.supportFragmentManager?.let { fm ->
                    modalBottomSheet.show(fm,
                        ModalBottomSheet.TAG)

                }
            }

    }

    override fun onPause() {
        super.onPause()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.apply {
                if (this.isExtended) {
                    this.shrink()
                }
            }

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener(null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_delete_24, null)
        val snoozeIcon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_access_time_24, null)

        val recyclerViewTaskList = binding.recyclerViewTasksList

        val deleteColor = resources.getColor(android.R.color.holo_red_light, null)
        val deleteColorDark = resources.getColor(android.R.color.holo_red_dark, null)
        val snoozeColor = resources.getColor(android.R.color.holo_orange_light, null)
        val snoozeColorDark = resources.getColor(android.R.color.holo_orange_dark, null)

        adapter = TaskListAdapter {
            val action = HomeFragmentDirections.actionGlobalTaskFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerViewTasksList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTask.collectLatest {
                    adapter.submitList(it)
                    for (i in it) {
                        Log.d("Home Fragment", "task ids = ${i.id}")
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

        // Define swipe helper here
        swipeHelper = ItemTouchHelper(SwipeHelper(
            RvColors(deleteColor, deleteColorDark, snoozeColor, snoozeColorDark),
            Icons(deleteIcon, snoozeIcon),
            Measurements(height, width),
            ViewUtil(adapter,
                requireView(),
                activity?.findViewById(R.id.fab_add),
                requireContext(),
                resources),
            viewModel, lifecycleScope
        ))
        swipeHelper.attachToRecyclerView(recyclerViewTaskList)

      /*  // Drag helper
        dragHelper = ItemTouchHelper(DragHelper(adapter, viewModel))
        dragHelper.attachToRecyclerView(recyclerViewTaskList)*/

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.action_global_action_settings)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}