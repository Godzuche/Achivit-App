package com.godzuche.achivitapp.feature_task.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                /*val emptyTask = Task(
                    title = "No task for today!",
                    description = "",
                    dueDate = 0,
                    collectionTitle = ""
                )
                TaskCard(task = emptyTask)*/
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
    state: HomeUiState = HomeUiState()
) {
    Card(
        onClick = {},
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .then(modifier)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (taskTitleTextRef, taskStatusColorRef, taskDueDateRef) = createRefs()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TodayTasksPreview() {
    TodayTasks(state = HomeUiState())
}