package com.godzuche.achivitapp.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcon
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.core.ui.util.capitalizeEachWord
import com.godzuche.achivitapp.core.ui.util.removeWidthConstraint
import com.godzuche.achivitapp.domain.repository.DarkThemeConfig

@Composable
fun SettingsRoute(
    onNavigateUp: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsUiState by settingsViewModel.settingsUiState.collectAsStateWithLifecycle()

    SettingsScreen(
        settingsUiState = settingsUiState,
        onChangeDarkThemeConfig = settingsViewModel::updateDarkThemeConfig,
        onBackPress = onNavigateUp
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsUiState: SettingsUiState,
    onChangeDarkThemeConfig: (DarkThemeConfig) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPress
                    ) {
                        Icon(
                            imageVector = AchivitIcons.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) {
        when (settingsUiState) {
            SettingsUiState.Loading -> Unit
            is SettingsUiState.Success -> {
                LazyVerticalGrid(
                    state = lazyGridState,
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                        .consumeWindowInsets(it),
                    columns = GridCells.Adaptive(300.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {

                        var isExpanded by rememberSaveable {
                            mutableStateOf(false)
                        }

                        SettingsRow(
                            title = "Dark mode",
                            startIcon = AchivitIcon.DrawableResourceIcon(
                                id = AchivitIcons.DeviceTheme
                            ),
                            modifier = Modifier
                                .removeWidthConstraint(16.dp)
                                .fillMaxWidth()
                                .clickable { isExpanded = true },
                            content = {
                                Text(
                                    text = settingsUiState.settings.darkThemeConfig.getDisplayableThemeConfig(),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                SettingsDropDownMenu(
                                    isExpanded = isExpanded,
                                    items = listOf(
                                        DarkThemeConfig.LIGHT,
                                        DarkThemeConfig.DARK,
                                        DarkThemeConfig.FOLLOW_SYSTEM
                                    ),
                                    getItemTitle = { darkThemeConfig ->
                                        darkThemeConfig.getDisplayableThemeConfig()
                                    },
                                    selectedItem = settingsUiState.settings.darkThemeConfig,
                                    onMenuItemClick = { darkThemeConfig ->
                                        isExpanded = false
                                        onChangeDarkThemeConfig(darkThemeConfig)
                                    },
                                    onDismissRequest = {
                                        isExpanded = false
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    startIcon: AchivitIcon?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (startIcon) {
            is AchivitIcon.ImageVectorIcon -> Icon(
                imageVector = startIcon.imageVector,
                contentDescription = null,
                modifier = Modifier.padding(end = 24.dp)
            )

            is AchivitIcon.DrawableResourceIcon -> Icon(
                painter = painterResource(id = startIcon.id),
                contentDescription = null,
                modifier = Modifier.padding(end = 24.dp)
            )

            else -> Box(modifier = Modifier.size(48.dp))
        }
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

@Composable
fun <T> SettingsDropDownMenu(
    isExpanded: Boolean,
    items: List<T>,
    getItemTitle: (T) -> String,
    selectedItem: T,
    onMenuItemClick: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest
    ) {
        items.forEach { item ->
            DarkThemeConfigMenuItem(
                title = getItemTitle(item),
                item = item,
                selectedItem = selectedItem,
                onClick = {
                    onMenuItemClick(item)
                }
            )
        }
    }
}

@Composable
fun <T> DarkThemeConfigMenuItem(
    title: String,
    item: T,
    selectedItem: T,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = {
            Text(text = title)
        },
        onClick = onClick,
        modifier = modifier.background(
            color = if (selectedItem == item) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                Color.Transparent
            }
        )
    )
}

@Preview
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(
            settingsUiState = SettingsUiState.Success(
                settings = UserEditableSettings(
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
                )
            ),
            onChangeDarkThemeConfig = {},
            onBackPress = {}
        )
    }
}

fun DarkThemeConfig.getDisplayableThemeConfig(): String =
    when (this) {
        DarkThemeConfig.FOLLOW_SYSTEM -> "System default"
        else -> this.name.capitalizeEachWord()
    }