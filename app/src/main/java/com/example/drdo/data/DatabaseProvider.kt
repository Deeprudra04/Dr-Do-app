package com.example.drdo.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: DrDoDatabase? = null

    fun getDatabase(context: Context): DrDoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DrDoDatabase::class.java,
                "drdo_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
} 