package com.godzuche.achivitapp.feature.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.core.design_system.components.GoogleSignInButton
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient

@Composable
fun AuthRoute(
    oneTapClient: SignInClient,
    navigateToHome: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val userAuthState by authViewModel.userAuthState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { activityResult ->
            val intent = activityResult.data ?: return@rememberLauncherForActivityResult

            authViewModel.signInWithGoogle(
                getSignInCredential = {
                    val credential = oneTapClient.getSignInCredentialFromIntent(intent)
                    credential
                }
            )
        }
    )

    when (userAuthState) {
        UserAuthState.Loading -> LoadingBox()
        is UserAuthState.SignedIn -> navigateToHome()
        else -> {
            AuthScreen(
                onSignInWithGoogleClick = {
                    authViewModel.requestOneTapSignIn()
                }
            )
            if (userAuthState is UserAuthState.Error) {
                val errorMessage = (userAuthState as UserAuthState.Error).e?.localizedMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    when (authUiState) {
        AuthUiState.NotLoading -> Unit
        AuthUiState.Loading -> LoadingBox()

        is AuthUiState.OneTapUi<*> -> {
            val beginSignInResult =
                (authUiState as AuthUiState.OneTapUi<*>).data as BeginSignInResult

            googleSignInLauncher.launch(
                IntentSenderRequest
                    .Builder(intentSender = beginSignInResult.pendingIntent.intentSender)
                    .build()
            )
        }

        is AuthUiState.Success -> {
            Toast.makeText(context, "Sign in successful", Toast.LENGTH_LONG).show()
            navigateToHome()
        }

        is AuthUiState.Error -> {
            val errorMessage = (authUiState as AuthUiState.Error).exception?.localizedMessage

            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
    }

}

@Composable
fun AuthScreen(
    onSignInWithGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        GoogleSignInButton(
            onClick = onSignInWithGoogleClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun LoadingBox() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}