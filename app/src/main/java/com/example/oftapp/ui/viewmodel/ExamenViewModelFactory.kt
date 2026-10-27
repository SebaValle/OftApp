package com.example.oftapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oftapp.data.repository.ExamenRepository

class ExamenViewModelFactory(private val repository: ExamenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExamenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExamenViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}