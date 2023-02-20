package com.godzuche.achivitapp.core.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

fun Modifier.removeWidthConstraint(contentPadding: Dp) =
    this.layout { measurable, constraints ->
        val placeable: Placeable = measurable.measure(
            constraints.copy(
                maxWidth = constraints.maxWidth + (contentPadding * 2).roundToPx()
            )
        )
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }