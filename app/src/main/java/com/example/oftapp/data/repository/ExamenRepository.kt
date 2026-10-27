package com.example.oftapp.data.repository

import com.example.oftapp.data.local.entity.ExamenEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExamenRepository(private val dummyDao: Any? = null) {

    private val _examenes = MutableStateFlow<List<ExamenEntity>>(emptyList())
    val todosLosExamenes: Flow<List<ExamenEntity>> = _examenes.asStateFlow()

    suspend fun insertar(examen: ExamenEntity) {
        val listaActual = _examenes.value.toMutableList()
        listaActual.add(0, examen)
        _examenes.value = listaActual
    }
}