package com.godzuche.achivitapp.feature_task.presentation.home

sealed class HomeUiEvent {
    data class Navigate(val screen: Screen) : HomeUiEvent()
    object NavigateBack : HomeUiEvent()
}