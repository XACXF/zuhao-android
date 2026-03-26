package com.gameaccount.manager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gameaccount.manager.data.local.converter.DateConverter
import com.gameaccount.manager.data.local.dao.AccountDao
import com.gameaccount.manager.data.local.dao.RecycleBinDao
import com.gameaccount.manager.data.local.entity.AccountEntity
import com.gameaccount.manager.data.local.entity.RecycleBinEntity

@Database(
    entities = [AccountEntity::class, RecycleBinEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AccountDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun recycleBinDao(): RecycleBinDao

    companion object {
        @Volatile
        private var INSTANCE: AccountDatabase? = null

        fun getDatabase(context: Context): AccountDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountDatabase::class.java,
                    "account_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
