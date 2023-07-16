package com.godzuche.achivitapp.core.design_system.components

import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = modifier
    ) {
        val iconWidth = 24.dp
        Icon(
            painterResource(id = AchivitIcons.Google24),
            contentDescription = null,
            tint = Color.Unspecified
        )
        Text(
            text = "Sign in with Google",
            modifier = Modifier
                .weight(1f)
                .offset(x = -iconWidth / 2),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

    }
}