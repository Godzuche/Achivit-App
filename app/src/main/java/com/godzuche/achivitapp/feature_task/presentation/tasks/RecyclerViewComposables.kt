package com.godzuche.achivitapp.feature_task.presentation.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TaskStatusColor(modifier: Modifier = Modifier, color: Color = Color.Transparent) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color = color.copy(alpha = 0.5f))
            .then(modifier)
    )
}

@Composable
fun DoneCheckBox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckChanged,
        modifier = Modifier
//                .size(48.dp)
            .then(modifier),
        colors = CheckboxDefaults.colors(
            checkedColor = Color(0xFF52D726),
            uncheckedColor = MaterialTheme.colorScheme.onBackground,
            checkmarkColor = MaterialTheme.colorScheme.onBackground
        )
    )
}