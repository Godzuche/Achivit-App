package com.godzuche.achivitapp.feature_task.presentation.ui_elements.task_details

import android.graphics.Color
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTaskBinding
import com.godzuche.achivitapp.feature_task.domain.model.Task
import com.godzuche.achivitapp.feature_task.presentation.TasksUiEvent
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_task.presentation.ui_elements.ModalBottomSheet
import com.godzuche.achivitapp.feature_task.presentation.util.SnackBarActions
import com.godzuche.achivitapp.feature_task.presentation.util.UiEvent
import com.godzuche.achivitapp.feature_task.presentation.util.task_frag_util.DateTimePickerUtil.setupDateTimePicker
import com.godzuche.achivitapp.feature_task.presentation.util.themeColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by activityViewModels()

    private val navigationArgs: TaskFragmentArgs by navArgs()

    private val modalBottomSheet = ModalBottomSheet()

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
        // Inflate the layout for this fragment
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        requireView().doOnPreDraw { startPostponedEnterTransition() }

        val id = navigationArgs.id

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.retrieveTask(id).collect { clickedTask ->
                    task = clickedTask
                    bind(task!!)
                    setupDateTimePicker(task!!, binding, viewModel)
                }
            }
        }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.PopBackStack -> {
                            findNavController().popBackStack()
                        }
                        is UiEvent.ShowSnackBar -> {
                            val snackBar =
                                Snackbar.make(binding.coordinator,
                                    event.message,
                                    Snackbar.LENGTH_LONG)
                                    .setAnchorView(activity?.findViewById(R.id.fab_add))

                            if (event.action == SnackBarActions.UNDO) {
                                snackBar.setAction(event.action) {
                                    viewModel.accept(TasksUiEvent.OnUndoDeleteClick)
                                }.show()
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetAction.emit("Edit Task")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bottomSheetTaskId.emit(navigationArgs.id)
            }
        }

    }

    private fun bind(task: Task) {
        var timeSuffix: String

        binding.apply {
            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description

            val formatter = SimpleDateFormat("E, MMM d", Locale.getDefault())
            // Calender instance
//            val calender = Calendar.getInstance()
            val dateSelection = task.date
            val formattedDateString = formatter.format(dateSelection)
            val mHour = when {
                task.hours == 12 -> {
                    timeSuffix = "PM"
                    task.hours
                }
                task.hours > 12 -> {
                    timeSuffix = "PM"
                    task.hours - 12
                }
                else -> {
                    timeSuffix = "AM"
                    task.hours
                }
            }

            binding.chipTime.apply {
                text = getString(
                    R.string.date_time, formattedDateString, mHour,
                    task.minutes,
                    timeSuffix)

                visibility = View.VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        /*  activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
              ?.visibility = View.GONE*/

        /*activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_edit_24, activity?.theme)*/


    }

    override fun onResume() {
        super.onResume()

        val fab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)

        fab?.apply {
            icon = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_baseline_edit_24,
                activity?.theme)
            if (this.isExtended) {
                this.shrink()
            }
        }

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener {
                if (!modalBottomSheet.isAdded) {
                    modalBottomSheet.show(childFragmentManager,
                        ModalBottomSheet.TAG + "_task_fragment")
                }

            }

    }

    override fun onPause() {
        super.onPause()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener(null)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete task")
            .setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Delete") { _, _ -> deleteTask() }
            .show()
    }

    private fun deleteTask() {
        task?.let {
            viewModel.accept(TasksUiEvent.OnDeleteTask(task = it,
                shouldPopBackStack = true))
        }
    }

}