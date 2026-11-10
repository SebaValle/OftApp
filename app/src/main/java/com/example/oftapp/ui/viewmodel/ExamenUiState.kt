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
    val rolSeleccionado: RolUsuario = RolUsuario.PACIENTE,
    val usuarioTexto: String = "",
    val passwordTexto: String = "",
    val errorLogin: String? = null,
    val pacienteNombre: String = "",
    val rut: String = "",
    val tipoExamen: String = "Campimetría / OD",
    val fecha: String = "27/10/2026",
    val observaciones: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val listaExamenes: List<ExamenEntity> = listOf(
        ExamenEntity(id = 1, pacienteNombre = "Pedro Gómez", rut = "11.111.111-1", tipoExamen = "Campimetría / OD", fecha = "27/10/2026", observaciones = "PENDIENTE DE VALIDACIÓN"),
        ExamenEntity(id = 2, pacienteNombre = "María González", rut = "22.222.222-2", tipoExamen = "TonoOcular / OI", fecha = "26/10/2026", observaciones = "VALIDADO POR MÉDICO"),
        ExamenEntity(id = 3, pacienteNombre = "Pedro Gómez", rut = "11.111.111-1", tipoExamen = "Fondo de Ojo", fecha = "25/10/2026", observaciones = "VALIDADO POR MÉDICO"),
        ExamenEntity(id = 4, pacienteNombre = "Carlos Pérez", rut = "33.333.333-3", tipoExamen = "Agudeza Visual / OD", fecha = "24/10/2026", observaciones = "PENDIENTE DE VALIDACIÓN")
    ),
    val filtroTexto: String = ""
) {
    // Métricas calculadas dinámicamente para el Dashboard de Admin
    val totalExamenes: Int get() = listaExamenes.size
    val totalValidados: Int get() = listaExamenes.count { it.observaciones.contains("VALIDADO", ignoreCase = true) }
    val totalPendientes: Int get() = listaExamenes.count { !it.observaciones.contains("VALIDADO", ignoreCase = true) }
    val porcentajeAvance: Float get() = if (totalExamenes > 0) (totalValidados.toFloat() / totalExamenes.toFloat()) else 0f
}