package com.godzuche.achivitapp.core.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.godzuche.achivitapp.core.data.local.database.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Upsert
    fun insertOrReplaceNotification(notification: NotificationEntity)

    @Query(
        value = """
            SELECT * FROM notifications
            ORDER BY notification_date DESC
        """
    )
    fun getNotificationEntities(): Flow<List<NotificationEntity>>

    @Delete
    fun delete(notification: NotificationEntity)

    @Query(
        value = """
            SELECT count(*) FROM notifications
        """
    )
    fun getCount(): Flow<Int>
}