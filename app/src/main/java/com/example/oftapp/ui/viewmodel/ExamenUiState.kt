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
    val pacienteNombre: String = "",
    val rut: String = "",
    val tipoExamen: String = "Campimetría / Ojo Derecho",
    val fecha: String = "27/10/2026",
    val observaciones: String = "",
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val listaExamenes: List<ExamenEntity> = listOf(
        ExamenEntity(id = 1, pacienteNombre = "Paciente Ficticio #1", rut = "11.111.111-1", tipoExamen = "Campimetría / Ojo Derecho", fecha = "27/10/2026", observaciones = "Estado: VALIDADO"),
        ExamenEntity(id = 2, pacienteNombre = "Paciente Ficticio #2", rut = "22.222.222-2", tipoExamen = "Campimetría / Ojo Derecho", fecha = "27/10/2026", observaciones = "Estado: VALIDADO"),
        ExamenEntity(id = 3, pacienteNombre = "Paciente Ficticio #3", rut = "33.333.333-3", tipoExamen = "Campimetría / Ojo Derecho", fecha = "27/10/2026", observaciones = "Estado: VALIDADO")
    ),
    val filtroTexto: String = ""
)