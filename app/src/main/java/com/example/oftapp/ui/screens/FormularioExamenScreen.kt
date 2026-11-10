package com.example.oftapp.ui.screens

import android.app.DatePickerDialog
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.viewmodel.ExamenViewModel
import com.example.oftapp.ui.viewmodel.RolUsuario
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioExamenScreen(
    viewModel: ExamenViewModel,
    onGuardadoExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)
    val context = LocalContext.current

    // Estado local para la foto capturada
    var fotoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher Nativo para Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoBitmap = bitmap
        }
    }

    // Configuración Nativa de DatePickerDialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
            viewModel.onFechaChange(fechaSeleccionada)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    var expandedTipo by remember { mutableStateOf(false) }
    val tiposExamen = listOf(
        "Campimetría / OD",
        "TonoOcular / OI",
        "Fondo de Ojo",
        "Agudeza Visual / OD",
        "Evaluación de Córnea"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.rolSeleccionado == RolUsuario.ADMINISTRADOR) "Registrar Atención Superusuario" else "Nuevo Examen Oftalmológico",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Datos de la Atención",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = azulHeader,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Nombre Paciente
            OutlinedTextField(
                value = uiState.pacienteNombre,
                onValueChange = { viewModel.onNombreChange(it) },
                label = { Text("Nombre del Paciente") },
                isError = uiState.errorNombre != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            if (uiState.errorNombre != null) {
                Text(
                    text = uiState.errorNombre!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }

            // RUT
            OutlinedTextField(
                value = uiState.rut,
                onValueChange = { viewModel.onRutChange(it) },
                label = { Text("RUT (ej: 11.111.111-1)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            )

            // Selector Tipo Examen Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = !expandedTipo },
                modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
            ) {
                OutlinedTextField(
                    value = uiState.tipoExamen,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Examen") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false }
                ) {
                    tiposExamen.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                viewModel.onTipoExamenChange(tipo)
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            // Selector Nativo de Fecha (DatePickerDialog)
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📅 Fecha del Examen: ${uiState.fecha}")
            }

            // Botón Nativo de Cámara
            OutlinedButton(
                onClick = { cameraLauncher.launch(null) },
                modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 14.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (fotoBitmap != null) "📷 Evidencia Capturada (Cambiar)" else "📷 Capturar Foto de Evidencia")
            }

            // Vista previa de la foto tomada
            fotoBitmap?.let { bitmap ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(bottom = 14.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Evidencia cámara",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Observaciones técnicas
            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = { viewModel.onObservacionesChange(it) },
                label = { Text("Observaciones Clínicas / Notas") },
                modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 20.dp)
            )

            // Botón Guardar
            Button(
                onClick = {
                    viewModel.guardarExamen {
                        onGuardadoExitoso()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("Guardar Examen", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onVolver) {
                Text("Cancelar y Volver", color = Color.Gray)
            }
        }
    }
}