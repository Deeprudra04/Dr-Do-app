package com.example.drdo.data

import androidx.room.Database
import androidx.room.RoomDatabase
 
@Database(entities = [Task::class, Attachment::class], version = 1)
abstract class DrDoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
} 