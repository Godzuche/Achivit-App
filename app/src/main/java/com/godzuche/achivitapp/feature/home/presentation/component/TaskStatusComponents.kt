package com.godzuche.achivitapp.feature.home.presentation.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.godzuche.achivitapp.core.design_system.theme.AchivitTypography
import com.godzuche.achivitapp.feature.home.presentation.HomeUiState
import com.godzuche.achivitapp.feature.home.presentation.TaskStatusOverview
import com.godzuche.achivitapp.core.ui.util.getFormattedName

@Composable
fun TaskStatusGrid(
    taskStatusOverviews: List<TaskStatusOverview>,
    modifier: Modifier = Modifier,
) {
    val lazyGridState = rememberLazyGridState()
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
            .then(modifier),
    ) {
        items(taskStatusOverviews, key = { it.taskStatus.ordinal }) { statusOverview ->
            TaskStatusOverviewCard(overview = statusOverview)
        }
    }
}

@Composable
fun TaskStatusOverviewCard(overview: TaskStatusOverview) {
    val animatedCount by animateIntAsState(targetValue = overview.taskCount, label = "Task Count")

    Card(
        onClick = {},
        modifier = Modifier.width(220.dp),
        colors = CardDefaults.cardColors(containerColor = overview.taskColor),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = animatedCount.toString(),
                style = AchivitTypography.titleMedium,
            )
            Text(
                text = overview.taskStatus.getFormattedName(),
                style = AchivitTypography.bodyMedium,
            )
        }
    }
}

@Preview
@Composable
fun TaskStatusGridPreview() {
    TaskStatusGrid(
        taskStatusOverviews = HomeUiState().copy(
            completedStatusOverview = TaskStatusOverview.Completed(
                count = 5
            )
        ).taskStatusOverviews,
    )
}