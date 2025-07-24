package com.example.drdo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attachment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val type: String, // e.g., "voice", "pdf", "image", "link", etc.
    val uri: String // file path or content uri
) 