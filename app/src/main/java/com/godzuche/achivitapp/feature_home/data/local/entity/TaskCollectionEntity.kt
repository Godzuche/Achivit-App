package com.godzuche.achivitapp.feature_home.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_collections")
data class TaskCollectionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category_title")
    val categoryTitle: String,
)