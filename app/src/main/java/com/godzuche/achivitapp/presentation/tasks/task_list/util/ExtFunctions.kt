package com.godzuche.achivitapp.presentation.tasks.task_list.util

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentTasksBinding
import com.godzuche.achivitapp.presentation.tasks.task_list.TaskListAdapter
import com.godzuche.achivitapp.presentation.tasks.task_list.TasksFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.ExperimentalCoroutinesApi

object ExtFunctions {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun TasksFragment.doOnScrolled(dy: Int) {
        val addTaskFab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        if (dy > 15 && addTaskFab?.isExtended == true) addTaskFab.shrink()
        else if (dy < -15 && addTaskFab?.isExtended == false) addTaskFab.extend()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun TasksFragment.doOnScrollChanged(
        recyclerView: RecyclerView,
        newState: Int,
        binding: FragmentTasksBinding
    ) {
        val addTaskFab = activity?.findViewById<ExtendedFloatingActionButton>(R.id.fab_add)
        /*val linearLayoutManager =
            (binding.recyclerViewTasksList.layoutManager as LinearLayoutManager)
        val adapter = recyclerView.adapter as TaskListAdapter
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                if (addTaskFab?.isExtended == false) addTaskFab.extend()
            } else if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.snapshot().items.size - 1
            ) {
                if (addTaskFab?.isExtended == false) addTaskFab.extend()
            }
        }*/
    }
}