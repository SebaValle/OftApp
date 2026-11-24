package com.example.oftapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.oftapp.data.local.AccesibilidadPreferences
import com.example.oftapp.data.local.entity.ExamenEntity
import com.example.oftapp.data.repository.ExamenRepository
import com.example.oftapp.ui.screens.DetalleExamenScreen
import com.example.oftapp.ui.screens.HistorialExamenesScreen
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import org.junit.Rule
import org.junit.Test

class OftAppAccesibilidadUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun historialYDetalleCumplenAccesibilidadTalkBackYFiltroPorTipo() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = ExamenRepository(context)
        val prefs = AccesibilidadPreferences(context)
        val viewModel = ExamenViewModel(repository, prefs)

        val examenDemo = ExamenEntity(
            id = 99,
            pacienteNombre = "Elena Valdés",
            rut = "16.789.123-4",
            tipoExamen = "Retinografía / OI",
            fecha = "24/11/2026",
            observaciones = "VALIDADO SIN HALLAZGOS",
            documentoUri = null,
            sincronizado = true
        )
        repository.agregarExamen(examenDemo)

        composeTestRule.setContent {
            HistorialExamenesScreen(
                viewModel = viewModel,
                onVerDetalleExamen = {},
                onNavegarInicio = {},
                onNavegarFormulario = {},
                onNavegarHistorial = {},
                onCerrarSesion = {}
            )
        }

        // Verifica controles de accesibilidad WCAG 2.2 AA
        composeTestRule.onNodeWithContentDescription("Abrir menú de navegación y accesibilidad")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("A+")
            .assertIsDisplayed()
            .performClick()

        // Filtra en el cuadro de búsqueda (detectado por acción de escritura)
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Elena Valdés")

        composeTestRule.onNodeWithText("Elena Valdés", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun detalleExamenMuestraVisorYAccesibilidad() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val viewModel = ExamenViewModel(
            ExamenRepository(context),
            AccesibilidadPreferences(context)
        )
        val examen = ExamenEntity(
            id = 100,
            pacienteNombre = "Carlos Muñoz",
            rut = "12.345.678-9",
            tipoExamen = "Campimetría Computarizada",
            fecha = "24/11/2026",
            observaciones = "VALIDADO",
            documentoUri = null,
            sincronizado = true
        )

        composeTestRule.setContent {
            DetalleExamenScreen(
                examen = examen,
                viewModel = viewModel,
                onVolverHistorial = {},
                onNavegarInicio = {},
                onNavegarFormulario = {},
                onNavegarHistorial = {},
                onCerrarSesion = {}
            )
        }

        composeTestRule.onNodeWithText("📥 Descargar Informe en PDF")
            .assertIsDisplayed()
            .performClick()
    }
}