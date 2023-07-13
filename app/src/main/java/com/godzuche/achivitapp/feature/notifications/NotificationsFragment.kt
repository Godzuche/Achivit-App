package com.godzuche.achivitapp.feature.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature.tasks.search_tasks.SearchFragmentDirections
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

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
    ): View? {
        // Inflate the layout for this fragment
        /*_binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root*/
        return ComposeView(requireContext()).apply {
            id = R.id.notification_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isTransitionGroup = true

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                Mdc3Theme(
                    context = this@NotificationsFragment.requireContext(),
                    setDefaultFontFamily = true
                ) {
                    NotificationRoute(
                        onNotificationClick = { notification ->
                            val action =
                                SearchFragmentDirections.actionGlobalTaskFragment(id = notification.id)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }

}