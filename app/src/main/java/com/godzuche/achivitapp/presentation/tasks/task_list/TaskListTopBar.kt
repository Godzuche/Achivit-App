package com.godzuche.achivitapp.presentation.tasks.task_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.godzuche.achivitapp.core.design_system.components.AchivitAssistChip
import com.godzuche.achivitapp.core.design_system.components.AchivitFilterChip
import com.godzuche.achivitapp.core.design_system.icon.AchivitIcons
import com.godzuche.achivitapp.data.local.database.model.TaskCategory

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksTopBar(
    categories: List<TaskCategory>,
    checkedCategoryId: Int,
    scrollBehaviour: TopAppBarScrollBehavior,
    onSelectCategory: (Int, String) -> Unit,
    onAddNewCategoryClick: () -> Unit,
    onSearchActionClick: () -> Unit,
    onFilterActionClick: () -> Unit,
    onSettingsActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        MediumTopAppBar(
            scrollBehavior = scrollBehaviour,
            modifier = modifier,
            title = { Text(text = "Tasks") },
            actions = {
                IconButton(onClick = onSearchActionClick) {
                    Icon(
                        imageVector = AchivitIcons.Search,
                        contentDescription = "Search tasks"
                    )
                }
                IconButton(onClick = onFilterActionClick) {
                    Icon(
                        imageVector = AchivitIcons.FilterList,
                        contentDescription = "Filter tasks"
                    )
                }
                IconButton(onClick = onSettingsActionClick) {
                    Icon(
                        imageVector = AchivitIcons.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            windowInsets = WindowInsets(0, 0, 0, 0)
        )
        val categoryFilterRowState = rememberLazyListState()
        val snappingLayout = remember(categoryFilterRowState) {
            SnapLayoutInfoProvider(
                lazyListState = categoryFilterRowState,
                positionInLayout = { layoutSize, itemSize, itemIndex ->
                    0
                }
            )
        }
        val flingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider = snappingLayout)

        LazyRow(
            state = categoryFilterRowState,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            flingBehavior = flingBehavior
        ) {
            items(items = categories) { taskCategory ->
                val index = categories.indexOf(taskCategory)
                val chipChecked = (index == checkedCategoryId)
                val focusRequester = remember {
                    FocusRequester()
                }
                val interactionSource = remember {
                    MutableInteractionSource()
                }
//                val isFocused by interactionSource.collectIsFocusedAsState()

                AchivitFilterChip(
                    selected = chipChecked,
                    onSelectedChange = { checked ->
                        focusRequester.requestFocus()
                        onSelectCategory(
                            index,
                            taskCategory.title
                        )
                    },
                    label = { Text(text = taskCategory.title) },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .focusable(interactionSource = interactionSource)
                )
            }
            item {
                AchivitAssistChip(
                    onClick = onAddNewCategoryClick,
                    label = { Text(text = "New category") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Add new category",
                            modifier = Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TaskListTopBarPreview() {
    val topBarState = rememberTopAppBarState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TasksTopBar(
            categories = listOf(
                TaskCategory(title = "Test Category", created = 0L)
            ),
            checkedCategoryId = 0,
            scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                topBarState
            ),
            onSelectCategory = { _, _ -> },
            onAddNewCategoryClick = {},
            onSearchActionClick = {},
            onFilterActionClick = {},
            onSettingsActionClick = {}
        )
    }
}