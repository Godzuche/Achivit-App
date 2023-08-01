package com.godzuche.achivitapp.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue

data class UserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: Long
)

data class NewExternalUserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: FieldValue
)

// For reading user from firestore
data class ExternalUserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: Timestamp
) {
    // Empty constructor needed to deserialization of firestore data to this class
    @Suppress("unused")
    constructor() : this("", null, null, null, Timestamp(0L, 0))
}

//For adding a new user to firestore db
fun FirebaseUser.toNewExternalUserData() = NewExternalUserData(
    userId = uid,
    displayName = displayName,
    email = email,
    profilePictureUrl = photoUrl.toString(),
    createdDate = FieldValue.serverTimestamp()
)

fun ExternalUserData.toUserData() = UserData(
    userId = userId,
    displayName = displayName,
    email = email,
    profilePictureUrl = profilePictureUrl,
    createdDate = createdDate.toDate().time
)

/*
fun FirebaseUser.toUserData() = UserData(
    userId = uid,
    displayName = displayName,
    email = email,
    profilePictureUrl = photoUrl.toString()
)
*/
