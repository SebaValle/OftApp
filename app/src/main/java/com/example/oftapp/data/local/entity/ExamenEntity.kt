package com.example.oftapp.data.local.entity

data class ExamenEntity(
    val id: Int = 0,
    val pacienteNombre: String,
    val rut: String,
    val tipoExamen: String,
    val fecha: String,
    val observaciones: String,
    val documentoUri: String? = null,
    val sincronizado: Boolean = false
)