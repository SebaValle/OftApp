package com.example.oftapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "examenes")
data class ExamenEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pacienteNombre: String,
    val rut: String,
    val tipoExamen: String,
    val fecha: String,
    val observaciones: String = ""
)