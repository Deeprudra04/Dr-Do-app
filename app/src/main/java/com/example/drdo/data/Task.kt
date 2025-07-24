package com.example.drdo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val dateTime: Long, // store as millis
    val reminder10Min: Boolean,
    val reminder1Day: Boolean
) 