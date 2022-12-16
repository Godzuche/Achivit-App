package com.godzuche.achivitapp.feature_task.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_task.presentation.tasks.TasksViewModel
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskViewModel: TasksViewModel by activityViewModels()

//    private val homeViewModel: HomeViewModel by viewModels()

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
                    val homeViewModel: HomeViewModel = viewModel()
                    val state by homeViewModel.uiState.collectAsState()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HomeScreen(
                            state = state,
                            onEvent = { event ->
                                exitTransition =
                                    MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
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
                                    is HomeUiEvent.Navigate -> {
                                        when (event.screen) {
                                            Screen.Settings -> {
                                                findNavController().navigate(HomeFragmentDirections.actionGlobalSettingsFragment())
                                            }
                                            Screen.Profile -> {
                                                findNavController().navigate(HomeFragmentDirections.actionGlobalActionProfile())
                                            }
                                            else -> Unit
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
    }
}