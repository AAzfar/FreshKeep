package com.personal.freshkeep.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [InventoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Thread-safe lock to prevent multiple database instances running concurrently
            return INSTANCE ?: synchronized(this) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freshkeep_database"
                )
                .fallbackToDestructiveMigration() // Tells Room to safely re-create tables when fields change during dev
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
