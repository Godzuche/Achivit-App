package com.godzuche.achivitapp.feature_task.presentation.home

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCategory
import com.godzuche.achivitapp.feature_task.data.local.entity.TaskCollection
import com.godzuche.achivitapp.feature_task.presentation.util.task_frag_util.DateTimePickerUtil

@Composable
fun CategoriesRow(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(
            state.categoryWithCollectionsPairs?.toList()
                ?: listOf(
                    Pair(
                        TaskCategory(
                            title = "Category is Empty",
                            created = Calendar.getInstance().timeInMillis
                        ),
                        emptyList<TaskCollection>()
                    )
                ),
            key = { categoryWithCollectionsPairs ->
                // use category title as key
                categoryWithCollectionsPairs.first.title
            }
        ) { categoryWithCollectionsPair ->
            categoryWithCollectionsPair.run {
                val taskCategory: TaskCategory = first
                val taskCollections: List<TaskCollection> = second
                CategoryCard(
                    categoryTitle = taskCategory.title,
                    collectionsCount = taskCollections.size,
                    created = taskCategory.created.millisToString(pattern = "MMM d, YYYY")
                )
            }
        }
        item(key = "create_category") {
            IconButton(
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

@Preview(showSystemUi = true)
@Composable
fun CategoryRowPreview() {
    CategoriesRow(state = HomeUiState())
}