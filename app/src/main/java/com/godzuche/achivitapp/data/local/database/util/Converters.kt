package com.godzuche.achivitapp.data.local.database.util

import androidx.room.TypeConverter
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
import kotlinx.datetime.Instant


class TaskStatusConverter {

    @TypeConverter
    fun fromStatus(status: TaskStatus) = status.name

    @TypeConverter
    fun toStatus(status: String) = enumValueOf<TaskStatus>(name = status)
}

class InstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}

/*
class JavaInstantConverter {
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let {
            ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(value),
                ZoneId.systemDefault()
            )
        }?.toInstant()

    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilli()
}

class ZonedDateTimeConverter {
    @TypeConverter
    fun ZonedDateTimeToLong(value: ZonedDateTime?): Long? =
        value?.toEpochSecond()?.times(1000)

    @TypeConverter
    fun LongToZonedDateTime(value: Long?): ZonedDateTime? =
        value?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
}*/
