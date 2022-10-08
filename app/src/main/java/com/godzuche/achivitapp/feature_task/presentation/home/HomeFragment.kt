package com.godzuche.achivitapp.feature_task.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentHomeBinding
import com.godzuche.achivitapp.feature_task.presentation.tasks.TasksViewModel
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @OptIn(ExperimentalCoroutinesApi::class)
    private val viewModel: TasksViewModel by activityViewModels()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            id = R.id.home_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Mdc3Theme {
                    HomeScreen(
                        onEvent = { event ->
                            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                                duration =
                                    resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                        .toLong()
                            }
                            reenterTransition =
                                MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                                    duration =
                                        resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                            .toLong()
                                }
                            when (event) {
                                is HomeEvent.Navigate -> {
                                    when (event.screen) {
                                        Screen.Settings -> {
                                            findNavController().navigate(HomeFragmentDirections.actionGlobalSettingsFragment())
                                        }
                                        Screen.Profile -> {
                                            findNavController().navigate(HomeFragmentDirections.actionGlobalActionProfile())
                                        }
                                        else -> {}
                                    }
                                }
                                else -> Unit
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

@Preview
@Composable
fun CategoriesRow(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(categoriesData) { category ->
            category.run {
                CategoryElement(
//                    drawable = drawable,
                    title = title,
                    started = started,
                    ends = ends,
                    timeLeft = timeLeft
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryElement(title: String, started: Int, ends: Int, timeLeft: Int) {
    Card(
        onClick = {},
        modifier = Modifier
            .height(56.dp)
            .width((LocalConfiguration.current.screenWidthDp.dp - 48.dp))
    ) {
    }
}

val categoriesData = List(size = 6) { i ->
    Category(
//        drawable = Icons.Filled.LibraryBooks,
        title = "Chemistry",
        started = i,
        ends = i + 1,
        timeLeft = (i + 1) - i
    )
}

data class Category(
//    val drawable: ImageVector,
    val title: String,
    val started: Int,
    val ends: Int,
    val timeLeft: Int
)

@Preview
@Composable
fun HomeSectionPreview() {
    Surface {
        HomeSection(title = "Categories", viewMoreButtonText = "View All") {
            CategoriesRow()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Home(modifier: Modifier = Modifier, innerPadding: PaddingValues) {
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
        item {
            HomeSection(
                title = "Categories",
                viewMoreButtonText = "View All",
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                CategoriesRow()
            }
        }
    }
}