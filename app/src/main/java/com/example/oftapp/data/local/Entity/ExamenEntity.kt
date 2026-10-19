package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pacienteId: String,
    val pacienteNombre: String,
    val tipoExamen: String,
    val ojoEvaluado: String, // Ej: "Ojo Derecho (OD)", "Ojo Izquierdo (OI)", "Ambos (AO)"
    val fecha: String,
    val sucursal: String,
    val profesional: String,
    val estado: String, // Ej: "Completado", "Pendiente", "En revisión"
    val observaciones: String,
    val documentoSimuladoUrl: String? = null
)