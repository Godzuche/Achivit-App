package com.godzuche.achivitapp.feature.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitDialog
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmActions
import com.godzuche.achivitapp.feature.tasks.task_list.ConfirmationDialog
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModels()

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
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        id = R.id.profile_fragment
        isTransitionGroup = true
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Mdc3Theme {
                val dialogState by profileViewModel.dialogState.collectAsStateWithLifecycle()
                if (dialogState.shouldShow) {
                    dialogState.dialog?.let { dialog ->
                        AchivitDialog(
                            achivitDialog = dialog,
                            onDismiss = {
                                profileViewModel.setDialogState(shouldShow = false)
                            },
                            onConfirm = {
                                when (dialog) {
                                    is ConfirmationDialog -> {
                                        when (dialog.action) {
                                            is ConfirmActions.SignOut -> run {
                                                profileViewModel.setDialogState(shouldShow = false)
                                                profileViewModel.signOut()
                                            }

                                            else -> Unit
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                ProfileRoute(
                    navigateToAuth = {
                        val action = ProfileFragmentDirections.actionActionProfileToAuth()
                        findNavController().navigate(action)
                    },
                    onSignOutClick = {
                        profileViewModel.setDialogState(
                            shouldShow = true,
                            dialog = ConfirmationDialog(
                                titleText = "Sign Out",
                                descriptionText = "Are you sure you want to sign out?",
                                confirmText = "Yes, sign-out",
                                cancelText = "No, cancel",
                                action = ConfirmActions.SignOut
                            )
                        )
                    }
                )
            }
        }
    }
}