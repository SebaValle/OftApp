package com.example.oftapp.data.repository

import android.content.Context
import com.example.oftapp.data.local.OftAppDbHelper
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.remote.InsForgeExamenDto
import com.example.oftapp.data.remote.InsForgeUsuarioDto
import com.example.oftapp.data.remote.RetrofitClient
import com.example.oftapp.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExamenRepository(context: Context) {

    private val dbHelper = OftAppDbHelper(context.applicationContext)

    private val _examenesFlow = MutableStateFlow<List<ExamenEntity>>(emptyList())
    val examenesFlow: StateFlow<List<ExamenEntity>> = _examenesFlow.asStateFlow()

    init {
        refrescarLista()
    }

    fun refrescarLista() {
        _examenesFlow.value = dbHelper.obtenerTodosLosExamenes()
    }

    fun agregarExamen(examen: ExamenEntity) {
        dbHelper.insertarExamen(examen)
        refrescarLista()
    }

    fun validarExamen(idExamen: Int) {
        dbHelper.validarExamen(idExamen)
        refrescarLista()
    }

    suspend fun autenticarUsuarioRemoto(username: String, password: String): InsForgeUsuarioDto? {
        return try {
            val apiKey = RetrofitClient.INSFORGE_ANON_KEY
            val bearer = "Bearer $apiKey"
            val userLimpio = username.trim().lowercase()
            val passwordEncriptada = SecurityUtils.hashPassword(password)

            val response = RetrofitClient.insForgeService.validarUsuarioRemoto(
                apiKey = apiKey,
                bearerToken = bearer,
                filtroUsername = "eq.$userLimpio",
                filtroPassword = "eq.$passwordEncriptada"
            )
            if (response.isSuccessful) {
                response.body()?.firstOrNull { it.activo }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registrarUsuarioEnInsForge(
        username: String,
        password: String,
        nombreCompleto: String,
        rut: String,
        rol: String
    ): Result<InsForgeUsuarioDto> {
        return try {
            val apiKey = RetrofitClient.INSFORGE_ANON_KEY
            val bearer = "Bearer $apiKey"
            val passwordEncriptada = SecurityUtils.hashPassword(password)

            val nuevoUsuario = InsForgeUsuarioDto(
                username = username.trim().lowercase(),
                password = passwordEncriptada,
                nombreCompleto = nombreCompleto.trim(),
                rut = rut.trim(),
                rol = rol.uppercase(),
                activo = true
            )
            val response = RetrofitClient.insForgeService.crearUsuarioRemoto(
                apiKey = apiKey,
                bearerToken = bearer,
                usuarios = listOf(nuevoUsuario)
            )
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                val detalle = response.errorBody()?.string() ?: "Usuario o RUT ya registrado"
                Result.failure(Exception("HTTP ${response.code()}: $detalle"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sincronizarPendientesConInsForge(): Result<Int> {
        val pendientes = dbHelper.obtenerTodosLosExamenes().filter { !it.sincronizado }
        if (pendientes.isEmpty()) return Result.success(0)

        var sincronizadosCount = 0
        val apiKey = RetrofitClient.INSFORGE_ANON_KEY
        val bearer = "Bearer $apiKey"

        return try {
            for (examen in pendientes) {
                val dto = InsForgeExamenDto(
                    pacienteNombre = examen.pacienteNombre,
                    rut = examen.rut,
                    tipoExamen = examen.tipoExamen,
                    fecha = examen.fecha,
                    observaciones = examen.observaciones,
                    documentoUri = examen.documentoUri
                )
                val response = RetrofitClient.insForgeService.subirExamen(
                    apiKey = apiKey,
                    bearerToken = bearer,
                    examenes = listOf(dto)
                )
                if (response.isSuccessful) {
                    dbHelper.marcarComoSincronizado(examen.id)
                    sincronizadosCount++
                } else {
                    val errorDetalle = response.errorBody()?.string() ?: "Sin detalle"
                    return Result.failure(Exception("HTTP ${response.code()}: $errorDetalle"))
                }
            }
            refrescarLista()
            Result.success(sincronizadosCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun consultarEjemploFhir(): String {
        return try {
            val response = RetrofitClient.fhirService.buscarReportesOftalmologicos(limite = 3)
            if (response.isSuccessful) {
                val total = response.body()?.total ?: 0
                val primerTexto = response.body()?.entry?.firstOrNull()?.resource?.code?.text ?: "DiagnosticReport R4"
                "HAPI FHIR R4 conectado ($total reportes • Ej: $primerTexto)"
            } else {
                "HAPI FHIR respondió HTTP ${response.code()}"
            }
        } catch (e: Exception) {
            "Sin conexión a HAPI FHIR (Modo Offline activo)"
        }
    }
}