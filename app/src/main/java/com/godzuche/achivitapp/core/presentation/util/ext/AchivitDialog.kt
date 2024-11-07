package com.godzuche.achivitapp.core.presentation.util.ext

import com.godzuche.achivitapp.core.domain.model.AchivitDialog
import com.godzuche.achivitapp.core.presentation.util.ConfirmationResourceProvider
import com.godzuche.achivitapp.core.presentation.util.DialogResourceProvider

fun AchivitDialog.getDialogResource(): DialogResourceProvider = when (this) {
    is AchivitDialog.ConfirmationDialog -> ConfirmationResourceProvider(action)
}