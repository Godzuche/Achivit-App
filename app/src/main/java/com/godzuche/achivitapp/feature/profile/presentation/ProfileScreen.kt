package com.godzuche.achivitapp.feature.profile.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.components.AchivitTopBar
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.design_system.theme.AchivitDimension
import com.godzuche.achivitapp.core.design_system.theme.Alpha
import com.godzuche.achivitapp.core.domain.model.UserData
import com.godzuche.achivitapp.feature.auth.presentation.UserAuthState
import com.godzuche.achivitapp.feature.auth.presentation.isNotNull
import timber.log.Timber

@Composable
fun ProfileRoute(
    navigateToAuth: () -> Unit,
    onSignOutClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userAuthState by profileViewModel.userAuthState.collectAsStateWithLifecycle()
    val profileUiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    ProfileScreen(
        userAuthState = userAuthState,
        profileUiState = profileUiState,
        onChangeProfilePhoto = profileViewModel::updateUserProfile,
        onSignOutClick = onSignOutClick,
        onSignedOut = navigateToAuth
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    userAuthState: UserAuthState,
    profileUiState: ProfileUiState,
    onChangeProfilePhoto: (Uri, String) -> Unit,
    onSignOutClick: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AchivitTopBar(
                title = {
                    Text(text = "Profile")
                }
            )
        }
    ) { padding ->
        when (userAuthState) {
            is UserAuthState.SignedIn -> {
                Column(
                    modifier = modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .windowInsetsPadding(WindowInsets(bottom = 84.dp))
                        .fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(AchivitDimension.minVerticalGridColumnWidth),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ProfileHeader(
                                userData = userAuthState.data,
                                onUpdateUserProfile = onChangeProfilePhoto
                            )
                        }
                    }

                    Button(
                        onClick = onSignOutClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.sign_out))
                    }

                }

                when (profileUiState) {
                    ProfileUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ProfileUiState.Success -> {
                        profileUiState.message?.let {
                            Toast.makeText(
                                context,
                                it,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    is ProfileUiState.Error -> {
                        Toast.makeText(
                            context,
                            profileUiState.exception?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            UserAuthState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UserAuthState.NotSignedIn -> onSignedOut()

            is UserAuthState.Error -> {
                Toast.makeText(context, userAuthState.e?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun ProfileHeader(
    userData: UserData,
    onUpdateUserProfile: (Uri, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        ProfileImage(
            userData = userData,
            onChangeProfilePhoto = {
                onUpdateUserProfile(it, userData.displayName ?: "")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userData.displayName ?: "Unknown",
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(4.dp))

        userData.email?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alpha(Alpha.MEDIUM_HIGH),
            )
        }
    }
}

@Composable
fun ProfileImage(
    userData: UserData,
    onChangeProfilePhoto: (Uri) -> Unit
) {
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> /*selectedImageUri = uri*/ uri?.let(onChangeProfilePhoto)
            if (uri.isNotNull()) {
                Timber.tag("PhotoPicker").i("Selected URI: $uri")
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        contentAlignment = Alignment.Center
    ) {
        val imageRequest = ImageRequest.Builder(context)
            .data(userData.profilePictureUrl ?: R.drawable.avatar_12)
            .crossfade(true)
            .size(Size.ORIGINAL)
            .build()

        AsyncImage(
            model = imageRequest,
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.High,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        ProfileImageEditButton(
            onClick = {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
    }

}

@Composable
private fun BoxScope.ProfileImageEditButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .align(Alignment.BottomEnd)
            .background(MaterialTheme.colorScheme.background)
            .padding(4.dp)

    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp),
            /*  colors = IconButtonDefaults.iconButtonColors(
                  containerColor = MaterialTheme.colorScheme.tertiaryContainer
              )*/
        ) {
            Icon(
                imageVector = AchivitIcons.Edit,
                contentDescription = "Edit button",
//                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        userAuthState = UserAuthState.SignedIn(
            data = UserData(
                userId = "",
                displayName = "God'swill Jonathan",
                email = "godzuche@gmail.com",
                profilePictureUrl = null,
                createdDate = 0L
            )
        ),
        profileUiState = ProfileUiState.Success(),
        onChangeProfilePhoto = { _, _ -> },
        onSignOutClick = {},
        onSignedOut = {}
    )
}