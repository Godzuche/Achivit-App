package com.godzuche.achivitapp

import android.app.Application
import com.godzuche.achivitapp.data.TaskRoomDatabase

class TaskApplication : Application() {
    val database by lazy {
        TaskRoomDatabase.getDatabase(this)
    }
}