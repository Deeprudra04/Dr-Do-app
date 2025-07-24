package com.example.drdo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drdo.data.DatabaseProvider
import com.example.drdo.data.Task
import com.example.drdo.data.Attachment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ManageWorksViewModel(application: Application) : AndroidViewModel(application) {
    private val db = DatabaseProvider.getDatabase(application)
    private val dao = db.taskDao()

    // Expose all tasks as StateFlow
    val tasks: StateFlow<List<Task>> = dao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getAttachmentsForTask(taskId: Long): StateFlow<List<Attachment>> =
        dao.getAttachmentsForTask(taskId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }

    fun addAttachment(attachment: Attachment) {
        viewModelScope.launch {
            dao.insertAttachment(attachment)
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            dao.deleteAttachment(attachment)
        }
    }
} 