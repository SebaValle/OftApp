package com.example.oftapp

import com.example.oftapp.data.local.AccesibilidadPreferences
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.remote.InsForgeUsuarioDto
import com.example.oftapp.data.repository.ExamenRepository
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExamenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ExamenRepository
    private lateinit var prefs: AccesibilidadPreferences
    private lateinit var viewModel: ExamenViewModel

    private val examenesFlow = MutableStateFlow<List<ExamenEntity>>(emptyList())
    private val escalaFlow = MutableStateFlow(1.0f)
    private val contrasteFlow = MutableStateFlow(false)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        prefs = mockk(relaxed = true)

        every { repository.examenesFlow } returns examenesFlow
        every { prefs.escalaTextoFlow } returns escalaFlow
        every { prefs.altoContrasteFlow } returns contrasteFlow

        viewModel = ExamenViewModel(repository, prefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarSesion valida campos vacios y autentica rol MEDICO`() = runTest {
        viewModel.iniciarSesion()
        assertEquals("Ingresa usuario y contraseña", viewModel.uiState.value.errorLogin)

        viewModel.onUsuarioTextoChange("dra.rojas")
        viewModel.onPasswordTextoChange("pass123")

        val dto = InsForgeUsuarioDto(
            id = 1,
            username = "dra.rojas",
            password = "hash",
            nombreCompleto = "Dra. Rojas",
            rut = "11.222.333-4",
            rol = "MEDICO",
            activo = true
        )
        coEvery { repository.autenticarUsuarioRemoto("dra.rojas", "pass123") } returns dto

        viewModel.iniciarSesion()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.sesionIniciada)
        assertEquals(RolUsuario.MEDICO, viewModel.uiState.value.rolSeleccionado)
        assertEquals("Dra. Rojas", viewModel.uiState.value.usuarioNombreCompleto)

        viewModel.cerrarSesion()
        assertFalse(viewModel.uiState.value.sesionIniciada)
    }

    @Test
    fun `crearCuentaUsuario valida datos y permite inicio de sesion automatico`() = runTest {
        var exitoCallback = false
        viewModel.crearCuentaUsuario("", "", "", "", "PACIENTE") { ok, _ ->
            exitoCallback = ok
        }
        assertFalse(exitoCallback)

        val creado = InsForgeUsuarioDto(
            id = 2,
            username = "nuevo",
            password = "hash",
            nombreCompleto = "Ana Díaz",
            rut = "19.888.777-6",
            rol = "PACIENTE",
            activo = true
        )
        coEvery {
            repository.registrarUsuarioEnInsForge("nuevo", "1234", "Ana Díaz", "19.888.777-6", "PACIENTE")
        } returns Result.success(creado)

        viewModel.crearCuentaUsuario("nuevo", "1234", "Ana Díaz", "19.888.777-6", "PACIENTE", true) { ok, _ ->
            exitoCallback = ok
        }
        advanceUntilIdle()

        assertTrue(exitoCallback)
        assertTrue(viewModel.uiState.value.sesionIniciada)
        assertEquals("19.888.777-6", viewModel.uiState.value.usuarioRut)
    }

    @Test
    fun `guardarExamen exige nombre de paciente y guarda examen valido`() {
        var guardado = false
        viewModel.guardarExamen { guardado = true }
        assertFalse(guardado)
        assertNotNull(viewModel.uiState.value.errorNombre)

        viewModel.onNombreChange("Luis Morales")
        viewModel.onRutChange("14.222.333-5")
        viewModel.onTipoExamenChange("OCT Macular")
        viewModel.onFechaChange("24/11/2026")
        viewModel.onObservacionesChange("VALIDADO POR MÉDICO")
        viewModel.onDocumentoSeleccionado("content://test/oct.pdf")

        viewModel.guardarExamen { guardado = true }

        assertTrue(guardado)
        verify { repository.agregarExamen(any()) }
        assertEquals("", viewModel.uiState.value.pacienteNombre)
    }

    @Test
    fun `accesibilidad escalaTexto y altoContraste persisten en DataStore`() = runTest {
        viewModel.onCambiarEscalaTexto(1.5f)
        viewModel.onToggleAltoContraste()
        advanceUntilIdle()

        assertEquals(1.5f, viewModel.uiState.value.escalaTexto)
        assertTrue(viewModel.uiState.value.altoContraste)
        coVerify { prefs.guardarEscalaTexto(1.5f) }
        coVerify { prefs.guardarAltoContraste(true) }
    }

    @Test
    fun `metricas del dashboard y sincronizacion funcionan correctamente`() = runTest {
        examenesFlow.value = listOf(
            ExamenEntity(1, "Pac 1", "1-9", "OCT", "24/11/2026", "VALIDADO", null, true),
            ExamenEntity(2, "Pac 2", "2-7", "Retina", "24/11/2026", "PENDIENTE", null, false)
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.totalExamenes)
        assertEquals(1, viewModel.uiState.value.totalValidados)
        assertEquals(1, viewModel.uiState.value.totalPendientes)
        assertEquals(0.5f, viewModel.uiState.value.porcentajeAvance, 0.01f)

        coEvery { repository.sincronizarPendientesConInsForge() } returns Result.success(1)
        coEvery { repository.consultarEjemploFhir() } returns "HAPI FHIR R4 conectado"

        viewModel.sincronizarConNube()
        advanceUntilIdle()

        assertTrue(viewModel.mensajeSync.value?.contains("InsForge: 1") == true)
        viewModel.limpiarMensajeSync()
        assertNull(viewModel.mensajeSync.value)
    }
}