package com.godzuche.achivitapp.feature.tasks.task_list.util

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.core.ui.util.toDp
import com.godzuche.achivitapp.core.domain.model.Task
import com.godzuche.achivitapp.feature.tasks.task_list.TasksUiEvent
import com.godzuche.achivitapp.feature.tasks.task_list.TasksViewModel
import com.godzuche.achivitapp.feature.tasks.util.SnackBarActions
import com.godzuche.achivitapp.feature.tasks.util.UiEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@ExperimentalCoroutinesApi
class SwipeDragHelper(
    private val colors: RvColors,
    private val icons: Icons,
    private val measurements: Measurements,
    private val viewUtil: ViewUtil,
    private val viewModel: TasksViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.START or ItemTouchHelper.END
) {
    lateinit var task: Task

    private var task2: Task? = null
    private var task1: Task? = null
    private var position: Int = NO_POSITION

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        viewHolder.itemView.elevation = 24F

        val initialPosition = viewHolder.bindingAdapterPosition
        val targetPosition = target.bindingAdapterPosition

        val fromTask = viewUtil.adapter.snapshot().items[initialPosition]
        val toTask = viewUtil.adapter.snapshot().items[targetPosition]

        task1 = fromTask
        task2 = toTask
        Log.d("OnMove", "fromPos = $task1 : toPos = $task2")

        viewUtil.adapter.notifyItemMoved(initialPosition, targetPosition)

        return true
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

        val fromTask = viewUtil.adapter.snapshot().items[fromPos]
        val toTask = viewUtil.adapter.snapshot().items[toPos]
        Log.d("OnMoved", "fromPos = $fromTask : toPos = $toTask")

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            viewHolder?.itemView?.apply {
                scaleY = 1.05F
                scaleX = 1.05F
                alpha = 0.8F
            }
        } else {
            viewHolder?.itemView?.apply {
                scaleY = 1.0F
                scaleX = 1.0F
                alpha = 1.0F
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            scaleY = 1.0F
            scaleX = 1.0F
            alpha = 1.0F

        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        position = viewHolder.bindingAdapterPosition
        task = viewUtil.adapter.snapshot().items[position]
        if (direction == ItemTouchHelper.START) {
            showDeleteConfirmationDialog(position)

        } else if (direction == ItemTouchHelper.END) {
            /*val snoozeIcon = viewHolder.itemView.findViewById<ImageView>(R.id.imv_ic_snooze)
            if (!snoozeIcon.isVisible) {
                snoozeIcon.visibility =
                    View.VISIBLE
            }else {
                snoozeIcon.visibility = View.GONE
            }*/
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

    private fun showDeleteConfirmationDialog(position: Int) {
        MaterialAlertDialogBuilder(viewUtil.context)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
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
        viewModel.accept(TasksUiEvent.OnDeleteTask(task))

        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is UiEvent.ShowSnackBar -> {
                            val snackBar =
                                Snackbar.make(
                                    viewUtil.requiredView,
                                    event.message,
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAnchorView(viewUtil.anchorView)

                            if (event.action == SnackBarActions.UNDO) {
                                snackBar.setAction(event.action) {
                                    viewModel.accept(TasksUiEvent.OnUndoDeleteClick)
                                }.show()
                            }
                        }

                        else -> Unit
                    }
                }
            }
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

        when {
            dX < (measurements.width / 3) && dX > 0 -> c.drawColor(colors.snoozeColorDark)
            dX > (measurements.width / 3) && dX > 0 -> c.drawColor(colors.snoozeColor)
            dX > (-measurements.width / 3) && dX < 0 -> c.drawColor(colors.deleteColorDark)
            dX < (-measurements.width / 3) && dX < 0 -> c.drawColor(colors.deleteColor)
            dX == 0f -> c.drawColor(
                viewUtil.resources.getColor(
                    R.color.rv_drag_action_background,
                    null
                )
            )

            !isCurrentlyActive -> c.drawColor(
                viewUtil.resources.getColor(
                    R.color.rv_drag_action_background,
                    null
                )
            )
        }

        /*val textMargin = resources.getDimension(R.dimen.text_margin)
            .roundToInt()*/
        val textMargin = viewUtil.resources.getDimension(R.dimen.text_margin)
            .roundToInt()
        if (icons.snoozeIcon != null) {
            icons.snoozeIcon.bounds = Rect(
                textMargin,
                viewHolder.itemView.top + textMargin + 8.toDp,
                textMargin + icons.snoozeIcon.intrinsicWidth,
                viewHolder.itemView.top + icons.snoozeIcon.intrinsicHeight
                        + textMargin + 8.toDp
            )
        }

        if (icons.deleteIcon != null) {
            icons.deleteIcon.bounds = Rect(
                measurements.width - textMargin - icons.deleteIcon.intrinsicWidth,
                viewHolder.itemView.top + textMargin + 8.toDp,
                measurements.width - textMargin,
                viewHolder.itemView.top + icons.deleteIcon.intrinsicHeight
                        + textMargin + 8.toDp
            )
        }

        if (dX > 0) {
            icons.snoozeIcon?.draw(c)
        } else if (dX < 0) {
            icons.deleteIcon?.draw(c)
        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }


    private fun swap(fromTask: Task, toTask: Task) {
        viewModel.reorderTasks(fromTask, toTask)
    }

    companion object {
        /*        const val NO_SWIPE = 0
                const val NO_DRAG = 0*/
        const val NO_POSITION = -1
    }

}