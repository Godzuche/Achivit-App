package com.godzuche.achivitapp.core.domain.model

import com.godzuche.achivitapp.core.data.remote.model.NetworkNewUserData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue

data class UserData(
    val userId: String,
    val displayName: String?,
    val email: String?,
    val profilePictureUrl: String?,
    val createdDate: Long
)


//For adding a new user to firestore db
fun FirebaseUser.asNewNetworkUserData() = NetworkNewUserData(
    userId = uid,
    displayName = displayName,
    email = email,
    profilePictureUrl = photoUrl.toString(),
    createdDate = FieldValue.serverTimestamp()
)