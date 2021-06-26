package com.twotothirdpower.morkborgcharactersheet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Character::class, Inventory::class, CharacterInventoryJoin::class], version = 1)
@TypeConverters(com.twotothirdpower.morkborgcharactersheet.util.DatabaseConverter::class)
abstract class CharacterDatabase : RoomDatabase() {
    /** Connect to database using DAO */
    abstract val characterDatabaseDAO: CharacterDatabaseDAO

    companion object {
        /**
         * INSTANCE lets us return database via getInstance without reinitializing it every time
         * kinda like a singleton pattern.
         *
         * The value of a volatile variable will never be cached, and all writes and
         * reads will be done to and from the main memory. It means that changes made by one
         * thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: CharacterDatabase? = null

        fun getInstance(context: Context): CharacterDatabase {
            /**
             * Use synchronized since it's possible that multiple threads may ask for a database at
             * the same time. The synchronized block ensures only one thread executes the contained
             * code at a time.
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            CharacterDatabase::class.java,
                            "character_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(object : Callback() {
                            // Prepopulate inventory table with default items on the first time the app runs
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                val defaultInventory = DefaultInventoryList(context).defaultInventoryList
                                ioThread {
                                    val datasource = getInstance(context).characterDatabaseDAO
                                    defaultInventory.forEach { inventory ->
                                        datasource.insertInventory(inventory)
                                    }
                                }
                            }
                        })
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}