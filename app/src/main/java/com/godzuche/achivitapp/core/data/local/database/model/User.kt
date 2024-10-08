package com.godzuche.achivitapp.core.data.local.database.model

import androidx.room.Entity

@Entity(tableName = "user")
data class User(
    val firstName: String,
    val lastName: String,
    val userName: String
)
