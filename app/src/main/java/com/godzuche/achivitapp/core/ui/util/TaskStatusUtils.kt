package com.godzuche.achivitapp.core.ui.util

import androidx.compose.ui.graphics.Color
import com.godzuche.achivitapp.core.design_system.theme.MGreen
import com.godzuche.achivitapp.core.design_system.theme.MOrange
import com.godzuche.achivitapp.core.domain.model.TaskStatus

fun String.nameAndColor() = when (this) {
    "NONE" -> capitalizeEachWord() to Color.Transparent
    "TODO" -> capitalizeEachWord() to Color.Gray.copy(alpha = 0.5f)
    "IN_PROGRESS" -> capitalizeEachWord() to MOrange.copy(alpha = 0.5f)
    "RUNNING_LATE" -> capitalizeEachWord() to Color.Red.copy(alpha = 0.5f)
    "COMPLETED" -> capitalizeEachWord() to MGreen.copy(alpha = 0.5f)
    else -> "Null" to Color.Transparent
}

fun TaskStatus.getFormattedName() = this.name.run {
    when (this) {
        "NONE" -> capitalizeEachWord()
        "TODO" -> capitalizeEachWord()
        "IN_PROGRESS" -> capitalizeEachWord()
        "RUNNING_LATE" -> capitalizeEachWord()
        "COMPLETED" -> capitalizeEachWord()
        else -> "Null"
    }
}

fun String.toModifiedStatusText() = when (this) {
    "NONE" -> capitalizeEachWord()
    "TODO" -> capitalizeEachWord()
    "IN_PROGRESS" -> capitalizeEachWord()
    "RUNNING_LATE" -> "Late"
    "COMPLETED" -> "Done"
    else -> "Null"
}

fun String.fromModifiedStatusText() = when (this) {
    "None" -> TaskStatus.NONE
    "Todo" -> TaskStatus.TODO
    "In Progress" -> TaskStatus.IN_PROGRESS
    "Late" -> TaskStatus.RUNNING_LATE
    "Done" -> TaskStatus.COMPLETED
    else -> "Null"
}

fun String.statusColor() = when (this) {
    "NONE" -> Color.Transparent
    "TODO" -> Color.Gray.copy(alpha = 0.5f)
    "IN_PROGRESS" -> MOrange.copy(alpha = 0.5f)
    "RUNNING_LATE" -> Color.Red.copy(alpha = 0.5f)
    "COMPLETED" -> MGreen.copy(alpha = 0.5f)
    else -> Color.Transparent
}