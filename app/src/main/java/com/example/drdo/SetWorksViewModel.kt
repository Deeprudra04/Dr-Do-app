package com.example.drdo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drdo.data.Attachment
import com.example.drdo.data.DatabaseProvider
import com.example.drdo.data.Task
import kotlinx.coroutines.launch

class SetWorksViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val dao = db.taskDao()

    suspend fun saveTaskWithAttachments(
        title: String,
        description: String?,
        dateTime: Long,
        reminder10Min: Boolean,
        reminder1Day: Boolean,
        attachments: List<Attachment>
    ): Long {
        val taskId = dao.insertTask(
            Task(
                title = title,
                description = description,
                dateTime = dateTime,
                reminder10Min = reminder10Min,
                reminder1Day = reminder1Day
            )
        )
        attachments.forEach { att ->
            dao.insertAttachment(att.copy(taskId = taskId))
        }
        return taskId
    }
} 