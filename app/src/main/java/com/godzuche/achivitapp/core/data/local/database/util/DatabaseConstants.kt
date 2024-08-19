package com.godzuche.achivitapp.core.data.local.database.util

object DatabaseConstants {
    const val DATABASE_NAME = "achivit_database"
    const val DATABASE_VERSION = 1

    const val CATEGORY_TABLE_NAME = "task_categories"
    const val COLLECTION_TABLE_NAME = "task_collections"
    const val TASK_TABLE_NAME = "tasks"

    object PrepopulatedData {
        const val DEFAULT_CATEGORY_TITLE = "My Tasks"
        const val DEFAULT_COLLECTION_TITLE = "All Tasks"
    }
}