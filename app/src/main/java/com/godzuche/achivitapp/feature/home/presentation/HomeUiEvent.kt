package com.godzuche.achivitapp.feature.home.presentation

sealed interface HomeUiEvent {
    data object AddTaskCategory : HomeUiEvent
}
