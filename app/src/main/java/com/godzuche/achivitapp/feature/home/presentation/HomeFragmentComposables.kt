package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.core.design_system.theme.AchivitTypography
import com.godzuche.achivitapp.core.design_system.theme.MGreen
import com.godzuche.achivitapp.core.design_system.theme.MOrange
import com.godzuche.achivitapp.core.ui.util.capitalizeEachWord
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus

@Composable
fun TaskStatusGrid(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val lazyGridState = rememberLazyGridState()
    val statuses = TaskStatus.values()
    LazyHorizontalGrid(
        state = lazyGridState,
        rows = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(max(220.dp, with(LocalDensity.current) { 220.sp.toDp() }))
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(16.dp))
            .then(modifier)
    ) {
        items(statuses.size) { index ->
            val count = when (statuses[index]) {
                TaskStatus.NONE -> state.noneStatusCount
                TaskStatus.TODO -> state.todosTaskCount
                TaskStatus.IN_PROGRESS -> state.inProgressTaskCount
                TaskStatus.RUNNING_LATE -> state.lateTasksCount
                TaskStatus.COMPLETED -> state.completedTasksCount
            }
            TaskStatusOverviewCard(
                status = statuses[index].name,
                count = count
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskStatusOverviewCard(status: String, count: Int) {

    val statusTitleAndColor = status.nameAndColor()

    val animatedCount by animateIntAsState(targetValue = count)

    Card(
        onClick = {},
        modifier = Modifier
            .width(220.dp),
        colors = CardDefaults.cardColors(containerColor = statusTitleAndColor.second)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = animatedCount.toString(),
                style = AchivitTypography.titleMedium
            )
            Text(
                text = statusTitleAndColor.first,
                style = AchivitTypography.bodyMedium
            )
        }
    }
}

fun String.nameAndColor() = when (this) {
    "NONE" -> capitalizeEachWord() to Color.Transparent
    "TODO" -> capitalizeEachWord() to Color.Gray.copy(alpha = 0.5f)
    "IN_PROGRESS" -> capitalizeEachWord() to MOrange.copy(alpha = 0.5f)
    "RUNNING_LATE" -> capitalizeEachWord() to Color.Red.copy(alpha = 0.5f)
    "COMPLETED" -> capitalizeEachWord() to MGreen.copy(alpha = 0.5f)
    else -> "Null" to Color.Transparent
}

fun String.toModifiedStatusText() = when (this) {
    "NONE" -> capitalizeEachWord()
    "TODO" -> capitalizeEachWord()
    "IN_PROGRESS" -> capitalizeEachWord()
    "RUNNING_LATE" -> "Late"
    "COMPLETED" -> "Done"
    else -> "Null"
}

fun String.fromModifiedStatusText() = when (this) {
    "None" -> TaskStatus.NONE
    "Todo" -> TaskStatus.TODO
    "In Progress" -> TaskStatus.IN_PROGRESS
    "Late" -> TaskStatus.RUNNING_LATE
    "Done" -> TaskStatus.COMPLETED
    else -> "Null"
}

fun String.statusColor() = when (this) {
    "NONE" -> Color.Transparent
    "TODO" -> Color.Gray.copy(alpha = 0.5f)
    "IN_PROGRESS" -> MOrange.copy(alpha = 0.5f)
    "RUNNING_LATE" -> Color.Red.copy(alpha = 0.5f)
    "COMPLETED" -> MGreen.copy(alpha = 0.5f)
    else -> Color.Transparent
}

@Preview
@Composable
fun TaskStatusGridPreview() {
    TaskStatusGrid(state = HomeUiState(completedTasksCount = 5))
}