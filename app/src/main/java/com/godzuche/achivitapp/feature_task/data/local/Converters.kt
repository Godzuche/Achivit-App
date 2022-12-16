package com.godzuche.achivitapp.feature_task.data.local

import androidx.room.TypeConverter
import com.godzuche.achivitapp.feature_task.presentation.util.TaskStatus


class Converters {

    @TypeConverter
    fun fromStatus(status: TaskStatus) = status.name

    @TypeConverter
    fun toStatus(status: String) = enumValueOf<TaskStatus>(name = status)
}