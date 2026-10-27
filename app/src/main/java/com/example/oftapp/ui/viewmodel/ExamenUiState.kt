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
    val tipoExamen: String = "Campimetría / Ojo Derecho",
    val fecha: String = "27/10/2026",
    val observaciones: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val listaExamenes: List<ExamenEntity> = listOf(
        ExamenEntity(id = 1, pacienteNombre = "Pedro Gómez", rut = "11.111.111-1", tipoExamen = "Campimetría / OD", fecha = "27/10/2026", observaciones = "PENDIENTE DE VALIDACIÓN"),
        ExamenEntity(id = 2, pacienteNombre = "María González", rut = "22.222.222-2", tipoExamen = "TonoOcular / OI", fecha = "26/10/2026", observaciones = "VALIDADO POR MÉDICO"),
        ExamenEntity(id = 3, pacienteNombre = "Pedro Gómez", rut = "11.111.111-1", tipoExamen = "Fondo de Ojo", fecha = "25/10/2026", observaciones = "VALIDADO POR MÉDICO")
    ),
    val filtroTexto: String = ""
)