package com.godzuche.achivitapp.presentation.tasks.search_tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.presentation.tasks.task_list.TasksViewModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.R.integer
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragment : Fragment() {

    private val tasksViewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = ComposeView(requireContext()).apply {
        id = R.id.search_tasks_fragment
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        isTransitionGroup = true
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Mdc3Theme {
                SearchRoute(
                    tasksViewModel = tasksViewModel,
                    onBackClick = {
                        findNavController().popBackStack()
                    },
                    onExploreTasksClick = {
                        val action = SearchFragmentDirections.actionGlobalActionTasks()
                        findNavController().navigate(action)
                    },
                    onTaskClick = {
                        val action = SearchFragmentDirections.actionGlobalTaskFragment(id = it)
                        findNavController().navigate(action)
                    }
                )
            }
        }
    }

    @Composable
    fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
        var state by remember {
            mutableStateOf(Lifecycle.Event.ON_ANY)
        }
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                state = event
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        return state
    }
}