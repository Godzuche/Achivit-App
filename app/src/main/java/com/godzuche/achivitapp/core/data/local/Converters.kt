package com.godzuche.achivitapp.core.data.local

import androidx.room.TypeConverter
import com.godzuche.achivitapp.feature_tasks_feed.presentation.util.TaskStatus


class Converters {

    @TypeConverter
    fun fromStatus(status: TaskStatus) = status.name

    @TypeConverter
    fun toStatus(status: String) = enumValueOf<TaskStatus>(name = status)
}