package com.godzuche.achivitapp.feature.home.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun CategoryCard(
    categoryTitle: String,
    collectionsCount: Int,
    created: String,
    tasksCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {},
        modifier = modifier
            .wrapContentHeight()
            .width((LocalConfiguration.current.screenWidthDp.dp - (72 + 4).dp))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (titleText, collectionsCountText, dateCreatedText, tasksCountText/*, stat*/) = createRefs()
            Text(
                text = categoryTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(titleText) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )
            Text(
                // Todo: Use string plural instead
                text = "$collectionsCount collection(s)",
                fontSize = 14.sp,
                modifier = Modifier
                    .constrainAs(collectionsCountText) {
                        top.linkTo(titleText.bottom)
                        start.linkTo(titleText.start)
                    }
            )
            Text(
                text = "Created: $created",
                fontSize = 14.sp,
                modifier = Modifier
                    .constrainAs(dateCreatedText) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(collectionsCountText.bottom)
                        start.linkTo(titleText.start)
                    }
            )
            Text(
                text = "$tasksCount Task(s)",
                fontSize = 14.sp,
                modifier = Modifier
                    .constrainAs(tasksCountText) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )

        }
    }
}

@Preview
@Composable
fun CategoryCardPreview() {
    CategoryCard(
        categoryTitle = "My Tasks",
        collectionsCount = 4,
        created = "Jan 17, 2022",
        tasksCount = 0
    )
}