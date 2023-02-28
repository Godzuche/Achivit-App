package com.godzuche.achivitapp.feature_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.fragment.app.Fragment
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FragmentProfileBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.transition.MaterialFadeThrough

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
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

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        /*_binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.imvUserProfileIcon.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Box(contentAlignment = Alignment.Center) {
                        val ctx = LocalContext.current
                        val imageRequest = ImageRequest.Builder(ctx)
                            .data(R.drawable.avatar)
                            .size(Size.ORIGINAL)
                            .crossfade(true)
                            .build()

                        AsyncImage(
                            model = imageRequest,
                            placeholder = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.High,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(56.dp)
                        )
                    }
                }
            }
        }
        return binding.root*/

        return ComposeView(requireContext()).apply {
            id = R.id.profile_fragment
            isTransitionGroup = true
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Scaffold(
                        topBar = {
                            Text("Profile")
                        }
                    ) {
                        ProfileScreen(
                            modifier = Modifier
                                .padding(it)
                                .consumeWindowInsets(it)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ProfileScreen(modifier: Modifier = Modifier) {
        Row {
            Image(painter = painterResource(R.drawable.avatar__10_), contentDescription = null)
            Column {
                //
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<Chip>(R.id.chip_add_category)?.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}