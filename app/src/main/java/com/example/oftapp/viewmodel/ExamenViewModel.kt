package com.example.oftapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AppDatabase
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.ExamenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExamenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExamenRepository

    // Lista de exámenes en tiempo real expuesta a las pantallas Compose
    val examenes: StateFlow<List<ExamenEntity>>

    init {
        val examenDao = AppDatabase.getDatabase(application).examenDao()
        repository = ExamenRepository(examenDao)
        examenes = repository.todosLosExamenes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Función para guardar un examen completo
    fun guardarExamen(
        pacienteId: String,
        pacienteNombre: String,
        tipoExamen: String,
        ojoEvaluado: String,
        fecha: String,
        sucursal: String,
        profesional: String,
        estado: String = "Completado",
        observaciones: String,
        documentoSimuladoUrl: String? = null,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            val nuevoExamen = ExamenEntity(
                pacienteId = pacienteId,
                pacienteNombre = pacienteNombre,
                tipoExamen = tipoExamen,
                ojoEvaluado = ojoEvaluado,
                fecha = fecha,
                sucursal = sucursal,
                profesional = profesional,
                estado = estado,
                observaciones = observaciones,
                documentoSimuladoUrl = documentoSimuladoUrl
            )
            repository.guardarExamen(nuevoExamen)
            onExito()
        }
    }
}