package com.godzuche.achivitapp.feature.home.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmActions
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmationDialog
import com.godzuche.achivitapp.feature.tasks.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    /*    @Inject
        lateinit var oneTapClient: SignInClient*/

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

                    val permissionDialogQueue = homeViewModel.visiblePermissionDialogQueue
                    val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { perms ->
                        perms.keys.forEach { permission ->
                            homeViewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
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

                    permissionDialogQueue
                        .reversed()
                        .forEach { permission ->
                            PermissionDialog(
                                permissionTextProvider = when (permission) {
                                    Manifest.permission.POST_NOTIFICATIONS -> {
                                        NotificationsPermissionTextProvider()
                                    }

                                    else -> return@forEach
                                },
                                isPermanentlyDeclined = shouldShowRequestPermissionRationale(
                                    permission
                                ).not(),
                                onDismiss = homeViewModel::dismissPermissionDialog,
                                onOkClicked = {
                                    homeViewModel.dismissPermissionDialog()
                                    multiplePermissionsResultLauncher.launch(
                                        arrayOf(permission)
                                    )
                                },
                                onGoToAppSettingsClick = {
                                    homeViewModel.dismissPermissionDialog()
                                    requireActivity().openAppSettings()
                                }
                            )
                        }
                    // Try to request permission once at the start of the app
                    LaunchedEffect(true) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                            multiplePermissionsResultLauncher.launch(
                                arrayOf(
                                    Manifest.permission.POST_NOTIFICATIONS,
                                )
                            )
                        } else {
                            //
                        }
                    }
                }

            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class NotificationsPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined notifications permission." +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to send notifications for proper functioning"
        }
    }
}

class ExactAlarmsPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined fine location permission." +
                    "You can go to the app settings to grant it."
        } else {
            "This app needs access to your fine location for proper functioning"
        }
    }
}

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClicked: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(text = "Permission Required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        confirmButton = {
            Text(
                text = if (isPermanentlyDeclined) {
                    "Grant Permission"
                } else {
                    "OK"
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick.invoke()
                        } else {
                            onOkClicked.invoke()
                        }
                    }
                    .padding(16.dp)
            )
        }
    )
}