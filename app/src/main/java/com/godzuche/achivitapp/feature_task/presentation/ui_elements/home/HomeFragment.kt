package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.godzuche.achivitapp.databinding.FragmentHomeBinding
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
        returnTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HomeRoute(
                    onNavigate = { navAction ->
                        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                            duration =
                                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                    .toLong()
                        }
                        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                            duration =
                                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                    .toLong()
                        }
//                    val action = TasksFragmentDirections.actionActionTasksToActionSettings()
                        findNavController().navigate(navAction)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNavigate: (NavDirections) -> Unit
) {
    Mdc3Theme {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onNavigate = onNavigate
                )
            }
        ) { innerPadding ->
            HomeScreen(innerPadding = innerPadding)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, innerPadding: PaddingValues) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .consumedWindowInsets(innerPadding),
        contentPadding = innerPadding,
        state = listState
    ) {
        item {
            TaskStatusGrid(
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

data class TaskStatusOverview(
    val taskCount: Int,
    val status: String,
    val taskColor: Color,
    val statusIcon: Icon? = null
)

val TASK_STATUS_OVERVIEWS = listOf(
    TaskStatusOverview(
        taskCount = 12,
        status = TaskStatus.TODO.name.lowercase().capitalize(),
        taskColor = Color.Gray
    ),
    TaskStatusOverview(
        taskCount = 8,
        status = TaskStatus.IN_PROGRESS.name.capitalizeEachWord(),
        taskColor = Color(0xFFFFA500)
    ),
    TaskStatusOverview(
        taskCount = 4,
        status = TaskStatus.RUNNING_LATE.name.capitalizeEachWord(),
        taskColor = Color.Red
    ),
    TaskStatusOverview(
        taskCount = 27,
        status = TaskStatus.COMPLETED.name.lowercase().capitalize(),
        taskColor = Color(0xFF52D726)
    )
)

// Util
fun String.capitalizeEachWord(): String = split("_").joinToString(" ") {
    it.lowercase().capitalize()
}