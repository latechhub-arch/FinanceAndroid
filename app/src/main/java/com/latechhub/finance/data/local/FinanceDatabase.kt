package com.latechhub.finance.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PendingSmsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun pendingSmsDao(): PendingSmsDao

    companion object {
        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FinanceDatabase::class.java,
                    "finance_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
