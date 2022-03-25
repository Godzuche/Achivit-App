package com.godzuche.achivitapp.ui.home_frag_util

import android.graphics.Canvas
import android.graphics.Rect
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.util.dp
import com.godzuche.achivitapp.data.model.Task
import com.godzuche.achivitapp.ui.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


lateinit var task: Task

class SwipeHelper(
    private val colors: RvColors,
    private val icons: Icons,
    private val measurements: Measurements,
    private val viewUtil: ViewUtil,
    private val viewModel: TaskViewModel,
    private val lifecycleScope: LifecycleCoroutineScope,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {
    private var task2: Task? = null
    private var task1: Task? = null
    private var position: Int = -1

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        viewHolder.itemView.elevation = 24F

        val initialPosition = viewHolder.bindingAdapterPosition
        val targetPosition = target.bindingAdapterPosition

        val fromTask = viewUtil.adapter.currentList[initialPosition]
        val toTask = viewUtil.adapter.currentList[targetPosition]

//        swap(fromTask, toTask)
        task1 = fromTask
        task2 = toTask

        /*      viewModel.updateTask(fromTask.copy(id = toTask.id))
              viewModel.updateTask(toTask.copy(id = task1.id))*/

        viewUtil.adapter.notifyItemMoved(initialPosition, targetPosition)

        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.apply {
                scaleY = 1.1F
                alpha = 0.8F
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            scaleY = 1.0F
            alpha = 1.0F

            if (task1 != null) {
                swap(task1!!, task2!!)
            }
        }
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int,
    ) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        position = viewHolder.bindingAdapterPosition
        task = viewUtil.adapter.currentList[position]

        if (direction == ItemTouchHelper.LEFT) {
            // Show confirmation dialog to delete task from db
            showConfirmationDialog(position)

        } else {
            Snackbar.make(
                viewUtil.requiredView,
                "Snoozed",
                Snackbar.LENGTH_SHORT
            ).setAnchorView(
                viewUtil.anchorView
            ).show()
            viewUtil.adapter.notifyItemChanged(position)
        }
    }

    private fun showConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(viewUtil.context)
            .setTitle("Attention")
            .setMessage("Are you sure you want to delete?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ ->
                viewUtil.adapter.notifyItemChanged(position)
            }
            .setPositiveButton("Yes") { _, _ ->
                deleteTask()
            }
            .show()

    }

    private fun deleteTask() {
        viewModel.deleteTask(task)
        Snackbar.make(
            viewUtil.requiredView,
            "Task Deleted!",
            Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                undoDelete()
            }
            .setAnchorView(
                viewUtil.anchorView
            )
            .show()
    }

    private fun undoDelete() {
        lifecycleScope.launch {
            viewModel.undoDelete(task)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean,
    ) {
        // Background color based on swipe direction
        when {
            dX < (measurements.width / 3) && dX > 0 -> c.drawColor(colors.snoozeColorDark)
            dX > (measurements.width / 3) && dX > 0 -> c.drawColor(colors.snoozeColor)
            dX > (-measurements.width / 3) && dX < 0 -> c.drawColor(colors.deleteColorDark)
            dX < (-measurements.width / 3) && dX < 0 -> c.drawColor(colors.deleteColor)
            dX == 0f -> c.drawColor(viewUtil.resources.getColor(R.color.rv_drag_action_background,
                null))
            !isCurrentlyActive -> c.drawColor(viewUtil.resources.getColor(R.color.rv_drag_action_background,
                null))
        }

        //2. Printing the icons
        /*val textMargin = resources.getDimension(R.dimen.text_margin)
            .roundToInt()*/
        val textMargin = viewUtil.resources.getDimension(R.dimen.text_margin)
            .roundToInt()
        if (icons.snoozeIcon != null) {
            icons.snoozeIcon.bounds = Rect(
                textMargin,
                viewHolder.itemView.top + textMargin + 8.dp,
                textMargin + icons.snoozeIcon.intrinsicWidth,
                viewHolder.itemView.top + icons.snoozeIcon.intrinsicHeight
                        + textMargin + 8.dp
            )
        }

        if (icons.deleteIcon != null) {
            icons.deleteIcon.bounds = Rect(
                measurements.width - textMargin - icons.deleteIcon.intrinsicWidth,
                viewHolder.itemView.top + textMargin + 8.dp,
                measurements.width - textMargin,
                viewHolder.itemView.top + icons.deleteIcon.intrinsicHeight
                        + textMargin + 8.dp
            )
        }

        //3. Drawing icon based upon direction swiped
        if (dX > 0) icons.snoozeIcon?.draw(c) else icons.deleteIcon?.draw(c)

        super.onChildDraw(c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive)
    }

    private fun swap(fromTask: Task, toTask: Task) {
        viewModel.updateTask(fromTask.copy(id = toTask.id))
        viewModel.updateTask(toTask.copy(id = fromTask.id))
    }

}