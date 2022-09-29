package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaskStatusGrid(modifier: Modifier = Modifier) {
    val statusOverviewData by remember { mutableStateOf(TASK_STATUS_OVERVIEWS) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp)
            .then(modifier)
    ) {
        items(statusOverviewData.size) { index ->
            TaskStatusOverviewCard(data = statusOverviewData[index])
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatusOverviewCard(data: TaskStatusOverview) {
    Card(
        onClick = {},
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(containerColor = data.taskColor.copy(alpha = 0.5f))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Text(text = data.taskCount.toString(), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = data.status, fontSize = 14.sp)
        }
    }
}