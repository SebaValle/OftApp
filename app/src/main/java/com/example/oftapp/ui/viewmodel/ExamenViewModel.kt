package com.example.oftapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.ExamenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExamenViewModel(private val repository: ExamenRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamenUiState())
    val uiState: StateFlow<ExamenUiState> = _uiState.asStateFlow()

    fun onUsuarioTextoChange(texto: String) {
        _uiState.update { it.copy(usuarioTexto = texto, errorLogin = null) }
    }

    fun onPasswordTextoChange(texto: String) {
        _uiState.update { it.copy(passwordTexto = texto, errorLogin = null) }
    }

    fun iniciarSesion() {
        val user = _uiState.value.usuarioTexto.trim().lowercase()
        val pass = _uiState.value.passwordTexto.trim()

        if (pass != "1234") {
            _uiState.update { it.copy(errorLogin = "Contraseña incorrecta (usa 1234)") }
            return
        }

        val rolInferido = when (user) {
            "pedro" -> RolUsuario.PACIENTE
            "tecno" -> RolUsuario.TECNOLOGO
            "medico" -> RolUsuario.MEDICO
            "admin" -> RolUsuario.ADMINISTRADOR
            else -> {
                _uiState.update { it.copy(errorLogin = "Usuario no válido (usa: pedro, tecno, medico o admin)") }
                return
            }
        }

        _uiState.update {
            it.copy(
                sesionIniciada = true,
                rolSeleccionado = rolInferido,
                errorLogin = null
            )
        }
    }

    fun cerrarSesion() {
        _uiState.update {
            it.copy(
                sesionIniciada = false,
                usuarioTexto = "",
                passwordTexto = "",
                errorLogin = null
            )
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(pacienteNombre = nombre, errorNombre = null) }
    }

    fun onRutChange(rut: String) {
        _uiState.update { it.copy(rut = rut, errorRut = null) }
    }

    fun onTipoExamenChange(tipo: String) {
        _uiState.update { it.copy(tipoExamen = tipo) }
    }

    fun onFechaChange(fecha: String) {
        _uiState.update { it.copy(fecha = fecha) }
    }

    fun onObservacionesChange(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun onFiltroChange(filtro: String) {
        _uiState.update { it.copy(filtroTexto = filtro) }
    }

    fun guardarExamen(onExito: () -> Unit) {
        val estadoActual = _uiState.value
        if (estadoActual.pacienteNombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "El nombre del paciente es obligatorio") }
            return
        }

        val nuevoId = (estadoActual.listaExamenes.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoExamen = ExamenEntity(
            id = nuevoId,
            pacienteNombre = estadoActual.pacienteNombre,
            rut = estadoActual.rut.ifBlank { "11.111.111-1" },
            tipoExamen = estadoActual.tipoExamen,
            fecha = estadoActual.fecha,
            observaciones = estadoActual.observaciones.ifBlank { "PENDIENTE DE VALIDACIÓN" }
        )

        _uiState.update {
            it.copy(
                listaExamenes = listOf(nuevoExamen) + it.listaExamenes,
                pacienteNombre = "",
                rut = "",
                observaciones = ""
            )
        }
        onExito()
    }

    fun validarExamen(idExamen: Int) {
        _uiState.update { estado ->
            val listaActualizada = estado.listaExamenes.map { examen ->
                if (examen.id == idExamen) {
                    examen.copy(observaciones = "VALIDADO POR MÉDICO")
                } else {
                    examen
                }
            }
            estado.copy(listaExamenes = listaActualizada)
        }
    }

    fun onCambiarEscalaTexto(nuevaEscala: Float) {
        _uiState.update { it.copy(escalaTexto = nuevaEscala) }
    }
}