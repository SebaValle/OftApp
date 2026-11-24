package com.example.oftapp.data.repository

import com.example.oftapp.data.local.entity.ExamenEntity

class ExamenRepository {
    private val examenesLocales = mutableListOf<ExamenEntity>()

    fun obtenerExamenes(): List<ExamenEntity> = examenesLocales

    fun agregarExamen(examen: ExamenEntity) {
        examenesLocales.add(examen)
    }
}