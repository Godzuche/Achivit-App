package com.godzuche.achivitapp.ui

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.TaskApplication
import com.godzuche.achivitapp.data.model.Task
import com.godzuche.achivitapp.databinding.FragmentTaskBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (activity?.application as TaskApplication).database.taskDao()
        )
    }

    private val navigationArgs: TaskFragmentArgs by navArgs()

    private val modalBottomSheet = ModalBottomSheet()

    lateinit var task: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

        val id = navigationArgs.id

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.retrieveTask(id).collect { clickedTask ->
                    task = clickedTask
                    bind(task)
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
        binding.apply {
            tvTaskTitle.text = task.title
            tvTaskDescription.text = task.description
        }
    }

    override fun onStart() {
        super.onStart()

        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<Chip>(R.id.chip_add_collection)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.GONE

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.icon =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_edit_24, activity?.theme)


    }

/*    override fun onStop() {
        super.onStop()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bottomSheetTaskId.emit(-1)
        }
    }*/

    override fun onResume() {
        super.onResume()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener {

                if (!modalBottomSheet.isAdded) {

                    activity?.supportFragmentManager?.let { fm ->
                        modalBottomSheet.show(fm,
                            ModalBottomSheet.TAG + "_task_fragment")
                    }

                }

            }

    }

    override fun onPause() {
        super.onPause()

        activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
            ?.setOnClickListener(null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_task, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_delete_task) {
            showConfirmationDialog()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete task")
            .setMessage("Are you sure you want to delete this task?")
            .setCancelable(false)
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Delete") { _, _ ->
                deleteTask()
            }
            .show()

    }

    private fun deleteTask() {
        findNavController().popBackStack()
        viewModel.deleteTask(task)
        Snackbar.make(
            requireView(),
            "Task Deleted!",
            Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                undoDelete()
            }
            .setAnchorView(
                activity?.findViewById(R.id.fab_add)
            )
            .show()
    }

    private fun undoDelete() {
        lifecycleScope.launch {
            viewModel.undoDelete(task)
        }
    }

}