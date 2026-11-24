package com.example.oftapp.ui.viewmodel

import com.example.oftapp.data.local.entity.ExamenEntity

enum class RolUsuario {
    PACIENTE,
    TECNOLOGO,
    MEDICO,
    ADMINISTRADOR
}

data class ExamenUiState(
    val sesionIniciada: Boolean = false,
    val usuarioTexto: String = "",
    val passwordTexto: String = "",
    val usuarioNombreCompleto: String = "",
    val usuarioRut: String = "",
    val rolSeleccionado: RolUsuario = RolUsuario.PACIENTE,
    val errorLogin: String? = null,
    val escalaTexto: Float = 1.0f,

    val pacienteNombre: String = "",
    val errorNombre: String? = null,
    val rut: String = "",
    val errorRut: String? = null,
    val tipoExamen: String = "Campimetría / OD",
    val fecha: String = "24/11/2026",
    val observaciones: String = "",
    val documentoUri: String? = null,

    val filtroTexto: String = "",
    val listaExamenes: List<ExamenEntity> = emptyList()
) {
    val totalExamenes: Int get() = listaExamenes.size
    val totalValidados: Int get() = listaExamenes.count { it.observaciones.contains("VALIDADO", ignoreCase = true) }
    val totalPendientes: Int get() = totalExamenes - totalValidados
    val porcentajeAvance: Float get() = if (totalExamenes > 0) totalValidados.toFloat() / totalExamenes.toFloat() else 0f
}