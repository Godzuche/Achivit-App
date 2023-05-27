package com.godzuche.achivitapp.feature.home.presentation

sealed class HomeUiEvent {
    data class Navigate(val screen: Screen) : HomeUiEvent()
    object NavigateBack : HomeUiEvent()
}