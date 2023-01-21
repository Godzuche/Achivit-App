package com.godzuche.achivitapp.feature_home.presentation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.core.util.capitalizeEachWord
import com.godzuche.achivitapp.feature_tasks.presentation.util.TaskStatus
import com.godzuche.achivitapp.ui.theme.MGreen
import com.godzuche.achivitapp.ui.theme.MOrange

@Composable
fun TaskStatusGrid(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val statuses = TaskStatus.values()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
//            .height(168.dp)
            .height(272.dp)
            .background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
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
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(containerColor = statusTitleAndColor.second)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = animatedCount.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = statusTitleAndColor.first,
                fontSize = 14.sp,
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

fun String.statusColor() = when (this) {
    "NONE" -> Color.Transparent
    "TODO" -> Color.Gray.copy(alpha = 0.5f)
    "IN_PROGRESS" -> MOrange.copy(alpha = 0.5f)
    "RUNNING_LATE" -> Color.Red.copy(alpha = 0.5f)
    "COMPLETED" -> MGreen.copy(alpha = 0.5f)
    else -> Color.Transparent
}

@Preview(showSystemUi = true)
@Composable
fun TaskStatusGridPreview() {
    TaskStatusGrid(state = HomeUiState(completedTasksCount = 5))
}