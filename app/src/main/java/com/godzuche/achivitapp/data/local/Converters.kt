package com.godzuche.achivitapp.data.local

import androidx.room.TypeConverter
import com.godzuche.achivitapp.feature.feed.util.TaskStatus


class Converters {

    @TypeConverter
    fun fromStatus(status: TaskStatus) = status.name

    @TypeConverter
    fun toStatus(status: String) = enumValueOf<TaskStatus>(name = status)
}