package com.godzuche.achivitapp.core.design_system.icon

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.godzuche.achivitapp.R

object AchivitIcons {
    val DateRange = Icons.Rounded.DateRange
    val Description = Icons.Rounded.Description
    val ChevronLeft = Icons.Rounded.ChevronLeft
    val Delete = Icons.Rounded.Delete
    val DeviceTheme = R.drawable.device_theme
    val AccessTime = Icons.Rounded.AccessTime
    val ArrowBack = Icons.Rounded.ArrowBack
    val Search = Icons.Rounded.Search
    val Close = Icons.Rounded.Close
    val History = Icons.Rounded.History
    val Check = Icons.Rounded.Check
    val Add = Icons.Rounded.Add
    val FilterList = Icons.Rounded.FilterList
    val Settings = Icons.Rounded.Settings
    val Account = Icons.Rounded.Person
    val Notifications = Icons.Rounded.Notifications
}

sealed interface AchivitIcon {
    data class ImageVectorIcon(val imageVector: ImageVector) : AchivitIcon
    data class DrawableResourceIcon(@DrawableRes val id: Int) : AchivitIcon
}