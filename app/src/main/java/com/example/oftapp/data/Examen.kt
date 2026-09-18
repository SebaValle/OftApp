package com.example.oftapp.data

data class Examen(
    val id: String,
    val pacienteId: String,
    val pacienteNombre: String,
    val tipoExamen: String,
    val ojoEvaluado: String,
    val fecha: String,
    val sucursal: String,
    val profesional: String,
    val estado: String,
    val observaciones: String,
    val documentoSimuladoUrl: String? = null
)