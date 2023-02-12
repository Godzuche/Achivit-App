package com.godzuche.achivitapp.data.local.entity

import androidx.room.Entity

@Entity(tableName = "user")
data class User(
    val firstName: String,
    val lastName: String,
    val userName: String
)
