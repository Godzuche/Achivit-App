package com.godzuche.achivitapp.domain.model

data class UserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?
)