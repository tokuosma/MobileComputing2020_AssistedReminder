package com.example.assistedreminder

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Reminder::class], exportSchema = false , version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun reminderDao() : ReminderDao

    companion object {
        @Volatile
        private var INSTANCE : AppDatabase ?= null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database")
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope:CoroutineScope
    ): RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.reminderDao())
                }
            }
        }

        suspend fun populateDatabase(reminderDao: ReminderDao) {
            // Delete all content here.
            reminderDao.deleteAll()

            // Add sample words.
            var reminder = Reminder(uid = 1, message = "Moi", location = "Täällä", time = System.currentTimeMillis() / 1000L)
            reminderDao.insert(reminder)

        }
    }
}
