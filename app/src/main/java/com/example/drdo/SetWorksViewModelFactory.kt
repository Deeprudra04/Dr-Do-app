package com.example.drdo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SetWorksViewModelFactory(
    private val application: android.app.Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetWorksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SetWorksViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 