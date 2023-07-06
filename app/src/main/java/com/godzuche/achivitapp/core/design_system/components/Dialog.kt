package com.godzuche.achivitapp.core.design_system.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.godzuche.achivitapp.presentation.tasks.task_list.AchivitDialog

@Composable
fun AchivitDialog(
    achivitDialog: AchivitDialog,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            achivitDialog.title?.let {
                Text(text = it)
            }
        },
        text = {
            achivitDialog.description?.let {
                Text(
                    text = it
                )
            }
        },
        dismissButton = {
            achivitDialog.dismissLabel?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDismiss.invoke() }
                        .padding(16.dp)
                )
            }
        },
        confirmButton = {
            achivitDialog.confirmLabel?.let {
                Text(
                    text = it.ifBlank { "OK" },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onConfirm.invoke()
                        }
                        .padding(16.dp)
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}