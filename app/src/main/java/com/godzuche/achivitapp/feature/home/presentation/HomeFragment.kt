package com.godzuche.achivitapp.feature.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmActions
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmationDialog
import com.godzuche.achivitapp.feature.tasks.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tasksViewModel: TasksViewModel by activityViewModels()

    @Inject
    lateinit var oneTapClient: SignInClient

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                    val dialogState by tasksViewModel.dialogState.collectAsStateWithLifecycle()
                    if (dialogState.shouldShow) {
                        dialogState.dialog?.let { dialog ->
                            AchivitDialog(
                                achivitDialog = dialog,
                                onDismiss = { tasksViewModel.setDialogState(shouldShow = false) },
                                onDismissRequest = { tasksViewModel.setDialogState(shouldShow = false) },
                                onConfirm = {
                                    when (dialog) {
                                        is ConfirmationDialog -> {
                                            when (val action = dialog.action) {
                                                is ConfirmActions.DeleteTask -> {
                                                    tasksViewModel.setDialogState(shouldShow = false)
                                                    tasksViewModel.accept(
                                                        TasksUiEvent.OnDeleteConfirm(
                                                            task = action.task
                                                        )
                                                    )
                                                }

                                                else -> Unit
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        HomeRoute(
                            onNavigateToTaskDetail = { taskId ->
                                exitTransition = MaterialElevationScale(false).apply {
                                    duration =
                                        resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                            .toLong()
                                }
                                reenterTransition = MaterialElevationScale(true).apply {
                                    duration =
                                        resources.getInteger(com.google.android.material.R.integer.material_motion_duration_medium_1)
                                            .toLong()
                                }

                                // Todo: Add a SharedElement transition animation.
                                val action =
                                    HomeFragmentDirections.actionGlobalTaskFragment(id = taskId)
                                findNavController().navigate(action)
                            },
                            onTopBarAction = { topBarAction ->
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
                                when (topBarAction) {
                                    HomeTopBarActions.SETTINGS -> {
                                        findNavController().navigate(HomeFragmentDirections.actionGlobalSettingsFragment())
                                    }

                                    HomeTopBarActions.PROFILE -> {
                                        findNavController().navigate(HomeFragmentDirections.actionGlobalActionProfile())
                                    }
                                }
                            }
                        )
                    }
                }

            }
        }
    }
}