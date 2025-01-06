package com.godzuche.achivitapp.feature.home.presentation

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.core.design_system.theme.AchivitTheme
import com.godzuche.achivitapp.core.domain.model.AchivitDialog
import com.godzuche.achivitapp.core.domain.model.ConfirmAction
import com.godzuche.achivitapp.core.presentation.util.ConfirmationResourceProvider
import com.godzuche.achivitapp.core.presentation.util.DialogResourceProvider
import com.godzuche.achivitapp.feature.tasks.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
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
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                AchivitTheme {
                    val dialogState by tasksViewModel.dialogState.collectAsStateWithLifecycle()
                    if (dialogState.shouldShow) {
                        dialogState.dialog?.let { dialog ->
                            AchivitDialog(
                                dialogResource = dialog.getDialogResource(),
                                onDismiss = { tasksViewModel.setDialogState(shouldShow = false) },
                                onDismissRequest = { tasksViewModel.setDialogState(shouldShow = false) },
                                onConfirm = {
                                    when (dialog) {
                                        is AchivitDialog.ConfirmationDialog -> {
                                            when (val action = dialog.action) {
                                                is ConfirmAction.DeleteTask -> {
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

                                        else -> Unit
                                    }
                                }
                            )
                        }
                    }

                    var shouldShowExactAlarmDialog by rememberSaveable {
                        mutableStateOf(true)
                    }
                    val permissionDialogQueue = homeViewModel.visiblePermissionDialogQueue
                    val multiplePermissionsResultLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { perms ->
                        perms.keys.forEach { permission ->
                            val isGranted = perms[permission] == true
                            homeViewModel.onPermissionResult(
                                permission = permission,
                                isGranted = isGranted
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
                    LaunchedEffect(key1 = Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            multiplePermissionsResultLauncher.launch(
                                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                            )
                        } else {
                            // No permission needed
                        }
                    }

                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        !requireContext().canScheduleExactAlarms() &&
                        shouldShowExactAlarmDialog
                    ) {
                        PermissionDialog(
                            permissionTextProvider = ExactAlarmPermissionTextProvider(),
                            isPermanentlyDeclined = false,
                            onDismiss = {
                                shouldShowExactAlarmDialog = false
                            },
                            onOkClicked = {
                                requireContext().requestExactAlarmPermission()
                                shouldShowExactAlarmDialog = false
                            },
                            onGoToAppSettingsClick = { }
                        )
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

// Check if the app can schedule exact alarms
fun Context.canScheduleExactAlarms(): Boolean {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
}

// Launch the permission request intent
private fun Context.requestExactAlarmPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.fromParts("package", packageName, null)
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Unable to open exact alarm settings", Toast.LENGTH_SHORT).show()
        }
    }
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String = ""
    fun getDescription(): String = ""
}

class ExactAlarmPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(): String {
        return "This app needs access to exact alarm to be able to send timely notifications. " +
                "This is required for proper functioning."
    }
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

fun AchivitDialog.getDialogResource(): DialogResourceProvider = when (this) {
    is AchivitDialog.ConfirmationDialog -> ConfirmationResourceProvider(action)
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
        properties = DialogProperties(usePlatformDefaultWidth = true),
        title = {
            Text(text = "Permission Required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                ).ifBlank { permissionTextProvider.getDescription() }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettingsClick.invoke()
                    } else {
                        onOkClicked.invoke()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally),
//                )
            ) {
                Text(
                    text = if (isPermanentlyDeclined) {
                        "Grant Permission"
                    } else {
                        "OK"
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            if (isPermanentlyDeclined) {
//                                onGoToAppSettingsClick.invoke()
//                            } else {
//                                onOkClicked.invoke()
//                            }
//                        }
//                        .padding(16.dp)
                )
            }
        }
    )
}
