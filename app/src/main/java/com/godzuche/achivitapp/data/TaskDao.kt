package com.godzuche.achivitapp.data

import androidx.room.*
import com.godzuche.achivitapp.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

}