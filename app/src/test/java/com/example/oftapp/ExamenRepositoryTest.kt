package com.example.oftapp

import com.example.oftapp.data.local.OftAppDbHelper
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.remote.FhirBundleResponse
import com.example.oftapp.data.remote.FhirCode
import com.example.oftapp.data.remote.FhirDiagnosticReport
import com.example.oftapp.data.remote.FhirEntry
import com.example.oftapp.data.remote.HapiFhirApiService
import com.example.oftapp.data.remote.InsForgeApiService
import com.example.oftapp.data.remote.InsForgeUsuarioDto
import com.example.oftapp.data.remote.LoincApiService
import com.example.oftapp.data.repository.ExamenRepository
import com.google.gson.JsonParser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class ExamenRepositoryTest {

    private lateinit var dbHelper: OftAppDbHelper
    private lateinit var insForgeService: InsForgeApiService
    private lateinit var fhirService: HapiFhirApiService
    private lateinit var loincService: LoincApiService
    private lateinit var repository: ExamenRepository

    private val examenPrueba = ExamenEntity(
        id = 1,
        pacienteNombre = "Camila Soto",
        rut = "18.456.789-0",
        tipoExamen = "Campimetría / OD",
        fecha = "24/11/2026",
        observaciones = "PENDIENTE DE VALIDACIÓN",
        documentoUri = null,
        sincronizado = false
    )

    @Before
    fun setup() {
        dbHelper = mockk(relaxed = true)
        insForgeService = mockk()
        fhirService = mockk()
        loincService = mockk()

        every { dbHelper.obtenerTodosLosExamenes() } returns listOf(examenPrueba)

        repository = ExamenRepository(
            dbHelper = dbHelper,
            insForgeService = insForgeService,
            fhirService = fhirService,
            loincService = loincService
        )
    }

    @Test
    fun `agregarExamen y validarExamen actualizan SQLite y refrescan flujo`() {
        repository.agregarExamen(examenPrueba)
        verify { dbHelper.insertarExamen(examenPrueba) }

        repository.validarExamen(1)
        verify { dbHelper.validarExamen(1) }
        assertEquals(1, repository.examenesFlow.value.size)
    }

    @Test
    fun `autenticarUsuarioRemoto retorna usuario activo cuando credenciales coinciden`() = runTest {
        val usuarioDto = InsForgeUsuarioDto(
            id = 1,
            username = "medico1",
            password = "hashed",
            nombreCompleto = "Dra. Elena Rojas",
            rut = "12.345.678-9",
            rol = "MEDICO",
            activo = true
        )
        coEvery {
            insForgeService.validarUsuarioRemoto(any(), any(), any(), any())
        } returns Response.success(listOf(usuarioDto))

        val resultado = repository.autenticarUsuarioRemoto("MEDICO1", "1234")
        assertNotNull(resultado)
        assertEquals("MEDICO", resultado?.rol)
    }

    @Test
    fun `autenticarUsuarioRemoto retorna null si usuario esta inactivo o falla HTTP`() = runTest {
        val usuarioInactivo = InsForgeUsuarioDto(
            id = 2,
            username = "user",
            password = "hash",
            nombreCompleto = "Test",
            rut = "1-9",
            rol = "PACIENTE",
            activo = false
        )
        coEvery {
            insForgeService.validarUsuarioRemoto(any(), any(), any(), any())
        } returns Response.success(listOf(usuarioInactivo))

        assertNull(repository.autenticarUsuarioRemoto("user", "1234"))

        coEvery {
            insForgeService.validarUsuarioRemoto(any(), any(), any(), any())
        } returns Response.error(401, "Unauthorized".toResponseBody())

        assertNull(repository.autenticarUsuarioRemoto("user", "1234"))
    }

    @Test
    fun `registrarUsuarioEnInsForge retorna success al crear usuario nuevo`() = runTest {
        val creado = InsForgeUsuarioDto(
            id = 3,
            username = "pac1",
            password = "hash",
            nombreCompleto = "Juan Pérez",
            rut = "15.111.222-3",
            rol = "PACIENTE",
            activo = true
        )
        coEvery {
            insForgeService.crearUsuarioRemoto(any(), any(), any())
        } returns Response.success(listOf(creado))

        val result = repository.registrarUsuarioEnInsForge("pac1", "1234", "Juan Pérez", "15.111.222-3", "PACIENTE")
        assertTrue(result.isSuccess)
        assertEquals("pac1", result.getOrNull()?.username)
    }

    @Test
    fun `sincronizarPendientesConInsForge sube examenes pendientes y los marca sincronizados`() = runTest {
        coEvery {
            insForgeService.subirExamen(any(), any(), any())
        } returns Response.success(emptyList())

        val resultado = repository.sincronizarPendientesConInsForge()
        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull())
        verify { dbHelper.marcarComoSincronizado(1) }
        coVerify(exactly = 1) { insForgeService.subirExamen(any(), any(), any()) }
    }

    @Test
    fun `buscarTerminologiaLoinc parsea respuesta oficial de NLM Clinical Tables`() = runTest {
        val jsonLoinc = JsonParser.parseString(
            """[1, ["70936-0"], null, [["70936-0", "Visual acuity panel"]]]"""
        ).asJsonArray

        coEvery { loincService.buscarCodigosLoinc(any(), any()) } returns Response.success(jsonLoinc)

        val codigos = repository.buscarTerminologiaLoinc("visual acuity")
        assertEquals(1, codigos.size)
        assertEquals("LOINC 70936-0 - Visual acuity panel", codigos.first())
    }

    @Test
    fun `consultarEjemploFhir integra HAPI FHIR R4 y LOINC`() = runTest {
        val bundle = FhirBundleResponse(
            resourceType = "Bundle",
            total = 42,
            entry = listOf(
                FhirEntry(
                    resource = FhirDiagnosticReport(
                        id = "rep-1",
                        status = "final",
                        code = FhirCode(text = "Retinal Fundus Exam")
                    )
                )
            )
        )
        val jsonLoinc = JsonParser.parseString(
            """[1, ["70936-0"], null, [["70936-0", "Visual acuity"]]]"""
        ).asJsonArray

        coEvery { fhirService.buscarReportesOftalmologicos(any(), any()) } returns Response.success(bundle)
        coEvery { loincService.buscarCodigosLoinc(any(), any()) } returns Response.success(jsonLoinc)

        val mensaje = repository.consultarEjemploFhir()
        assertTrue(mensaje.contains("HAPI FHIR R4 conectado (42 reportes"))
        assertTrue(mensaje.contains("LOINC 70936-0"))
    }
}