package com.godzuche.achivitapp.data.remote.model

import com.godzuche.achivitapp.domain.model.UserData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

// For reading user from firestore
data class NetworkUserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: Timestamp
) {
    // Empty constructor needed for deserialization of firestore data to this class
    @Suppress("unused")
    constructor() : this("", null, null, null, Timestamp(0L, 0))
}

data class NetworkNewUserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: FieldValue
)

fun NetworkUserData.asExternalModel() = UserData(
    userId = userId,
    displayName = displayName,
    email = email,
    profilePictureUrl = profilePictureUrl,
    createdDate = createdDate.toDate().time
)