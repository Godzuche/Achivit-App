package com.godzuche.achivitapp.feature_task.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.godzuche.achivitapp.feature_task.domain.model.Task

@Composable
fun TodayTasks(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        state.todayTasks.run {
            if (this.isEmpty()) {
                Text(text = "No task for today!")
            } else {
                take(n = 3).forEach {
                    TaskCard(/*state = state,*/ task = it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    modifier: Modifier = Modifier,
//    state: HomeUiState = HomeUiState()
) {
    val statusColor by animateColorAsState(targetValue = task.status.name.statusColor())

    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column()
        {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val (taskTitleTextRef, taskStatusColorRef, taskDueDateRef) = createRefs()
                Text(
                    text = task.title,
                    modifier
                        .padding(vertical = 8.dp)
                        .constrainAs(taskTitleTextRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        },
                    fontSize = 16.sp
                )

                /* Box(
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(8.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .background(color = statusColor)
                         .constrainAs(taskStatusColorRef) {
                             start.linkTo(parent.start)
                             end.linkTo(parent.end)
                             bottom.linkTo(parent.bottom)
                         }
                 )*/
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = statusColor)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TodayTasksPreview() {
    TodayTasks(
        state = HomeUiState(
            todayTasks = listOf(
                Task(
                    title = "Read",
                    description = "Read a book",
                    dueDate = 0L,
                    collectionTitle = "New Collection"
                )
            )
        )
    )
}