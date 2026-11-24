package com.example.oftapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oftapp.data.local.AccesibilidadPreferences
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.ExamenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamenViewModel(
    private val repository: ExamenRepository,
    private val accesibilidadPrefs: AccesibilidadPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExamenUiState())
    val uiState: StateFlow<ExamenUiState> = _uiState.asStateFlow()

    private val _sincronizando = MutableStateFlow(false)
    val sincronizando: StateFlow<Boolean> = _sincronizando.asStateFlow()

    private val _mensajeSync = MutableStateFlow<String?>(null)
    val mensajeSync: StateFlow<String?> = _mensajeSync.asStateFlow()

    init {
        viewModelScope.launch {
            repository.examenesFlow.collect { listaDb ->
                _uiState.update { it.copy(listaExamenes = listaDb) }
            }
        }
        viewModelScope.launch {
            accesibilidadPrefs.escalaTextoFlow.collect { escalaGuardada ->
                _uiState.update { it.copy(escalaTexto = escalaGuardada) }
            }
        }
    }

    fun onUsuarioTextoChange(texto: String) {
        _uiState.update { it.copy(usuarioTexto = texto, errorLogin = null) }
    }

    fun onPasswordTextoChange(texto: String) {
        _uiState.update { it.copy(passwordTexto = texto, errorLogin = null) }
    }

    fun iniciarSesion() {
        val user = _uiState.value.usuarioTexto.trim().lowercase()
        val pass = _uiState.value.passwordTexto.trim()

        if (user.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorLogin = "Ingresa usuario y contraseña") }
            return
        }

        viewModelScope.launch {
            val usuarioNube = repository.autenticarUsuarioRemoto(user, pass)

            if (usuarioNube != null) {
                val rolInferido = when (usuarioNube.rol.uppercase()) {
                    "PACIENTE" -> RolUsuario.PACIENTE
                    "TECNOLOGO" -> RolUsuario.TECNOLOGO
                    "MEDICO" -> RolUsuario.MEDICO
                    "ADMINISTRADOR" -> RolUsuario.ADMINISTRADOR
                    else -> RolUsuario.PACIENTE
                }
                _uiState.update {
                    it.copy(
                        sesionIniciada = true,
                        rolSeleccionado = rolInferido,
                        usuarioNombreCompleto = usuarioNube.nombreCompleto,
                        usuarioRut = usuarioNube.rut,
                        errorLogin = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(errorLogin = "Usuario o contraseña incorrectos (verifica tu conexión)")
                }
            }
        }
    }

    fun crearCuentaUsuario(
        username: String,
        password: String,
        nombreCompleto: String,
        rut: String,
        rol: String,
        iniciarSesionAutomatico: Boolean = false,
        onResultado: (Boolean, String) -> Unit
    ) {
        if (username.isBlank() || password.isBlank() || nombreCompleto.isBlank() || rut.isBlank()) {
            onResultado(false, "Completa todos los campos obligatorios.")
            return
        }

        viewModelScope.launch {
            val resultado = repository.registrarUsuarioEnInsForge(
                username = username,
                password = password,
                nombreCompleto = nombreCompleto,
                rut = rut,
                rol = rol
            )
            resultado.onSuccess { usuarioCreado ->
                if (iniciarSesionAutomatico) {
                    _uiState.update {
                        it.copy(
                            sesionIniciada = true,
                            usuarioTexto = usuarioCreado.username,
                            usuarioNombreCompleto = usuarioCreado.nombreCompleto,
                            usuarioRut = usuarioCreado.rut,
                            rolSeleccionado = RolUsuario.PACIENTE,
                            errorLogin = null
                        )
                    }
                }
                onResultado(true, "Cuenta '${usuarioCreado.username}' (${usuarioCreado.rol}) creada exitosamente.")
            }.onFailure { error ->
                onResultado(false, "Error al registrar: ${error.localizedMessage}")
            }
        }
    }

    fun cerrarSesion() {
        _uiState.update {
            it.copy(
                sesionIniciada = false,
                usuarioTexto = "",
                passwordTexto = "",
                usuarioNombreCompleto = "",
                usuarioRut = "",
                errorLogin = null
            )
        }
    }

    fun onCambiarEscalaTexto(nuevaEscala: Float) {
        _uiState.update { it.copy(escalaTexto = nuevaEscala) }
        viewModelScope.launch {
            accesibilidadPrefs.guardarEscalaTexto(nuevaEscala)
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

    fun onDocumentoSeleccionado(uri: String?) {
        _uiState.update { it.copy(documentoUri = uri) }
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

        val nuevoExamen = ExamenEntity(
            pacienteNombre = estadoActual.pacienteNombre,
            rut = estadoActual.rut.ifBlank { "Sin RUT" },
            tipoExamen = estadoActual.tipoExamen,
            fecha = estadoActual.fecha,
            observaciones = estadoActual.observaciones.ifBlank { "PENDIENTE DE VALIDACIÓN" },
            documentoUri = estadoActual.documentoUri,
            sincronizado = false
        )

        repository.agregarExamen(nuevoExamen)

        _uiState.update {
            it.copy(
                pacienteNombre = "",
                rut = "",
                observaciones = "",
                documentoUri = null
            )
        }
        onExito()
    }

    fun validarExamen(idExamen: Int) {
        repository.validarExamen(idExamen)
    }

    fun sincronizarConNube() {
        if (_sincronizando.value) return
        viewModelScope.launch {
            _sincronizando.value = true
            _mensajeSync.value = "Sincronizando con InsForge y consultando HAPI FHIR..."

            val resultadoSync = repository.sincronizarPendientesConInsForge()
            val estadoFhir = repository.consultarEjemploFhir()

            resultadoSync.onSuccess { cantidad ->
                _mensajeSync.value = "☁️ InsForge: $cantidad examen(es) subido(s).\n🏥 $estadoFhir"
            }.onFailure { error ->
                _mensajeSync.value = "⚠️ Modo Offline: No se pudo conectar con InsForge (${error.localizedMessage ?: "sin red"})."
            }
            _sincronizando.value = false
        }
    }

    fun limpiarMensajeSync() {
        _mensajeSync.value = null
    }
}