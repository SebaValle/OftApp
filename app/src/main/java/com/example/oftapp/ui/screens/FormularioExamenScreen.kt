package com.example.oftapp.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.components.OftAppScaffold
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioExamenScreen(
    viewModel: ExamenViewModel,
    onNavegarInicio: () -> Unit,
    onNavegarFormulario: () -> Unit,
    onNavegarHistorial: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val azulHeader = Color(0xFF0066B2)

    // Selector seguro de PDF e Imágenes (Sin permisos de cámara ni crashes)
    val selectorDocumentoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Algunos proveedores de archivos temporales no requieren persistencia
            }
            viewModel.onDocumentoSeleccionado(uri.toString())
        }
    }

    // Calendario conectado directamente a uiState.fecha
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaFormateada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            viewModel.onFechaChange(fechaFormateada)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val tiposExamen = listOf(
        "Campimetría / OD",
        "Campimetría / OI",
        "TonoOcular / OD",
        "TonoOcular / OI",
        "Fondo de Ojo",
        "Agudeza Visual / OD"
    )
    var menuTipoExpandido by remember { mutableStateOf(false) }

    OftAppScaffold(
        tituloBarra = "OftApp - Nuevo Examen",
        viewModel = viewModel,
        pantallaActual = "formulario",
        onNavegarInicio = onNavegarInicio,
        onNavegarFormulario = onNavegarFormulario,
        onNavegarHistorial = onNavegarHistorial,
        onCerrarSesion = onCerrarSesion
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Registro Clínico Oftalmológico",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = azulHeader
            )

            OutlinedTextField(
                value = uiState.pacienteNombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre Completo del Paciente *") },
                isError = uiState.errorNombre != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (uiState.errorNombre != null) {
                Text(
                    text = uiState.errorNombre!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            OutlinedTextField(
                value = uiState.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT del Paciente (ej: 11.111.111-1)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = menuTipoExpandido,
                onExpandedChange = { menuTipoExpandido = !menuTipoExpandido }
            ) {
                OutlinedTextField(
                    value = uiState.tipoExamen,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Examen / Ojo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoExpandido) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = menuTipoExpandido,
                    onDismissRequest = { menuTipoExpandido = false }
                ) {
                    tiposExamen.forEach { seleccion ->
                        DropdownMenuItem(
                            text = { Text(seleccion) },
                            onClick = {
                                viewModel.onTipoExamenChange(seleccion)
                                menuTipoExpandido = false
                            }
                        )
                    }
                }
            }

            // Botón de Calendario
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📅 Fecha Agendada: ${uiState.fecha}")
            }

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones Técnicas (Opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            // Botón para adjuntar PDF o Imagen Clínica
            OutlinedButton(
                onClick = {
                    selectorDocumentoLauncher.launch(arrayOf("application/pdf", "image/*"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📄 Adjuntar Examen (PDF o Imagen)")
            }

            // Confirmación visual del archivo adjunto
            if (uiState.documentoUri != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "✔ Documento adjunto listo para guardar",
                            color = Color(0xFF2E7D32),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { viewModel.onDocumentoSeleccionado(null) }) {
                            Text("Quitar", color = Color.Red, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.guardarExamen {
                        onNavegarHistorial()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("💾 Guardar Examen", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = onNavegarInicio,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar y Volver", color = Color.Gray)
            }
        }
    }
}