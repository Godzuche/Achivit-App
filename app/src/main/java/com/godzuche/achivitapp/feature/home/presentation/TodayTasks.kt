package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.domain.model.Task

@Composable
fun TodayTasks(
    homeUiState: HomeUiState,
    onTaskClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        homeUiState.todayTasks.run {
            if (this.isEmpty()) {
                Text(
                    text = "No task for today!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                take(3).forEach { task ->
                    TodayTaskCard(
                        task = task,
                        onTaskClick = { task.id?.let { onTaskClick(it) } }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayTaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor by animateColorAsState(
        targetValue = task.status.name.statusColor(),
        label = "Task Status Color"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {

        Text(
            text = task.dueDate.millisToString("h:mm aa").replace(" ", "\n"),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 3.dp,
                color = statusColor
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(0.05f))
                Card(
                    onClick = onTaskClick,
                    modifier = Modifier
                        .weight(0.85f)
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .wrapContentHeight()
                    ) {
                        val (box, column) = createRefs()
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = statusColor)
                                .constrainAs(box) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(parent.start)
                                }
                                .height(72.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterVertically
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .constrainAs(column) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(box.end)
                                }
                        ) {
                            Text(
                                text = task.title,
//                                modifier = Modifier
//                            .fillMaxWidth()
//                                    .padding(24.dp),
//                            .padding(vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (task.description.isNotEmpty()) {
                                Text(
                                    text = task.description,
//                                modifier = Modifier
//                            .fillMaxWidth()
//                                    .padding(24.dp),
//                            .padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(0.1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodayTasksPreview() {
    TodayTasks(
        homeUiState = HomeUiState(
            todayTasks = listOf(
                Task(
                    title = "Read",
                    description = "Read a book",
                    created = 0L,
                    dueDate = 0L,
                    collectionTitle = "New Collection",
                    categoryTitle = "New Category"
                )
            )
        ),
        onTaskClick = {}
    )
}