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

    fun seleccionarRol(rol: RolUsuario) {
        _uiState.update { it.copy(rolSeleccionado = rol) }
    }

    fun iniciarSesion() {
        _uiState.update { it.copy(sesionIniciada = true) }
    }

    fun cerrarSesion() {
        _uiState.update { it.copy(sesionIniciada = false) }
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

    fun guardarExamen(onExito: () -> Unit) {
        val estado = _uiState.value
        if (estado.pacienteNombre.isBlank()) {
            _uiState.update { it.copy(errorNombre = "El nombre es obligatorio") }
            return
        }

        viewModelScope.launch {
            val nuevoExamen = ExamenEntity(
                pacienteNombre = estado.pacienteNombre,
                rut = estado.rut.ifBlank { "N/A" },
                tipoExamen = estado.tipoExamen,
                fecha = estado.fecha.ifBlank { "27/10/2026" },
                observaciones = if (estado.observaciones.isBlank()) "Estado: VALIDADO" else "Estado: VALIDADO - ${estado.observaciones}"
            )
            repository.insertar(nuevoExamen)
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