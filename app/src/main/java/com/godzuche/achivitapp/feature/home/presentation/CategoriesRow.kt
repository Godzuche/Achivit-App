package com.godzuche.achivitapp.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.ui.util.millisToString
import com.godzuche.achivitapp.core.domain.model.TaskCategory

@Composable
fun CategoriesRow(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val rowState = rememberLazyListState()
    LazyRow(
        modifier = modifier,
        state = rowState,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            items = state.categoryWithCollectionsAndTasks,
            key = { categoryWithCollectionsAndTasks ->
                // use category title as key since it is unique to each categories
                val taskCategory = categoryWithCollectionsAndTasks.category
                taskCategory.title
            }
        ) { categoryWithCollectionsAndTasks ->
            categoryWithCollectionsAndTasks.run {
                val taskCategory: TaskCategory = category
                val taskCollectionsWithTasks = this.collectionWithTasks
                CategoryCard(
                    categoryTitle = taskCategory.title,
                    collectionsCount = taskCollectionsWithTasks.size,
                    created = taskCategory.created.millisToString(pattern = "MMM d, YYYY"),
                    tasksCount = taskCollectionsWithTasks.run {
                        if (this.isNotEmpty()) {
                            sumOf { it.tasks.size }
                        } else 0
                    }
                )
            }
        }
        item {
            FilledIconButton(
                onClick = {},
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_new_category),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun CategoryRowPreview() {
    CategoriesRow(state = HomeUiState())
}