package com.godzuche.achivitapp.feature_tasks_feed.presentation.util

sealed class UiEvent {
    object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()

    //    data class ScrollToTop(val lastScrollPosition: Int) : UiEvent()
    object ScrollToTop : UiEvent()
    object ScrollToBottom : UiEvent()
    data class ShowSnackBar(
        val message: String,
        val action: String? = null,
    ) : UiEvent()
}