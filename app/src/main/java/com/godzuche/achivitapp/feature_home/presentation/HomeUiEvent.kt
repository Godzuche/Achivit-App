package com.godzuche.achivitapp.feature_home.presentation

sealed class HomeUiEvent {
    data class Navigate(val screen: Screen) : HomeUiEvent()
    object NavigateBack : HomeUiEvent()
}