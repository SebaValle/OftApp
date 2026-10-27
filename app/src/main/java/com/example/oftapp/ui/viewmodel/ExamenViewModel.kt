package com.example.oftapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.ExamenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamenViewModel(private val repository: ExamenRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamenUiState())
    val uiState: StateFlow<ExamenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.todosLosExamenes.collect { examenes ->
                if (examenes.isNotEmpty()) {
                    _uiState.update { it.copy(listaExamenes = examenes) }
                }
            }
        }
    }

    fun onUsuarioTextoChange(texto: String) {
        _uiState.update { it.copy(usuarioTexto = texto, errorLogin = null) }
    }

    fun onPasswordTextoChange(texto: String) {
        _uiState.update { it.copy(passwordTexto = texto, errorLogin = null) }
    }

    // Autenticación con usuarios simplificados
    fun iniciarSesion() {
        val estado = _uiState.value
        val user = estado.usuarioTexto.trim().lowercase()
        val pass = estado.passwordTexto.trim()

        if (pass != "1234") {
            _uiState.update { it.copy(errorLogin = "Contraseña incorrecta (Usa 1234)") }
            return
        }

        when (user) {
            "pedro" -> {
                _uiState.update {
                    it.copy(
                        sesionIniciada = true,
                        rolSeleccionado = RolUsuario.PACIENTE,
                        usuarioTexto = "11.111.111-1", // RUT asociado a Pedro Gómez
                        errorLogin = null
                    )
                }
            }
            "tecno" -> {
                _uiState.update {
                    it.copy(
                        sesionIniciada = true,
                        rolSeleccionado = RolUsuario.TECNOLOGO,
                        errorLogin = null
                    )
                }
            }
            "medico" -> {
                _uiState.update {
                    it.copy(
                        sesionIniciada = true,
                        rolSeleccionado = RolUsuario.MEDICO,
                        errorLogin = null
                    )
                }
            }
            "admin" -> {
                _uiState.update {
                    it.copy(
                        sesionIniciada = true,
                        rolSeleccionado = RolUsuario.ADMINISTRADOR,
                        errorLogin = null
                    )
                }
            }
            else -> {
                _uiState.update { it.copy(errorLogin = "Usuario no registrado. Usa: pedro, tecno, medico o admin") }
            }
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

    fun onObservacionesChange(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun onFechaChange(fecha: String) {
        _uiState.update { it.copy(fecha = fecha) }
    }

    fun onFiltroChange(filtro: String) {
        _uiState.update { it.copy(filtroTexto = filtro) }
    }

    fun validarExamen(id: Int) {
        _uiState.update { estadoActual ->
            val listaActualizada = estadoActual.listaExamenes.map { examen ->
                if (examen.id == id) {
                    examen.copy(observaciones = "VALIDADO POR MÉDICO - Sin Hallazgos Patológicos")
                } else {
                    examen
                }
            }
            estadoActual.copy(listaExamenes = listaActualizada)
        }
    }

    fun guardarExamen(onExito: () -> Unit) {
        val estado = _uiState.value
        if (estado.pacienteNombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            val nuevoExamen = ExamenEntity(
                id = (estado.listaExamenes.maxOfOrNull { it.id } ?: 0) + 1,
                pacienteNombre = estado.pacienteNombre,
                rut = estado.rut.ifBlank { "11.111.111-1" },
                tipoExamen = estado.tipoExamen,
                fecha = estado.fecha.ifBlank { "27/10/2026" },
                observaciones = if (estado.observaciones.isBlank()) "PENDIENTE DE VALIDACIÓN" else "PENDIENTE DE VALIDACIÓN: ${estado.observaciones}"
            )

            _uiState.update { it.copy(listaExamenes = it.listaExamenes + nuevoExamen) }
            limpiarFormulario()
            onExito()
        }
    }

    private fun limpiarFormulario() {
        _uiState.update {
            it.copy(
                pacienteNombre = "",
                rut = "",
                observaciones = "",
                errorNombre = null
            )
        }
    }
}