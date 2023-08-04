package com.godzuche.achivitapp.feature.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : Fragment() {

    @Inject
    lateinit var oneTapClient: SignInClient

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(context = requireContext()).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        id = R.id.auth_fragment

        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            Mdc3Theme {
                AuthRoute(
                    oneTapClient = oneTapClient,
                    navigateToHome = {
                        val action = AuthFragmentDirections.actionGlobalActionHome()
                        findNavController().navigate(action)
                    }
                )
            }
        }

    }

    override fun onStart() {
        super.onStart()

        /*lifecycleScope.launch {
            authViewModel.userAuthState.collectLatest { userAuthState ->
                when (userAuthState) {
                    is UserAuthState.SignedIn -> {
                        val action = AuthFragmentDirections.actionGlobalActionHome()
                        findNavController().navigate(action)
                    }

                    else -> Unit
                }
            }
        }*/

        /*if (firebaseAuth.currentUser.isNotNull()) {
            val action = AuthFragmentDirections.actionGlobalActionHome()
            findNavController().navigate(action)
        }*/
    }

}