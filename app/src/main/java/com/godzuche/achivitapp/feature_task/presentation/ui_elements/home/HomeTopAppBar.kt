package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDirections
import com.godzuche.achivitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    name: String = "Jonathan",
    activeTasks: Int = 5,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigate: (NavDirections) -> Unit
) {
    val ctx = LocalContext.current
    TopAppBar(
        title = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = ctx.resources.getString(R.string.greeting, name),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ctx.resources.getString(R.string.activeTasksMessage, activeTasks),
                    fontSize = 14.sp
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {},
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = { onNavigate(HomeFragmentDirections.actionGlobalSettingsFragment()) }) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings")
            }
        },
        scrollBehavior = scrollBehavior
    )
}