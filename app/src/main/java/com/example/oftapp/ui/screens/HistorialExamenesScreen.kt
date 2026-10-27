package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.viewmodel.ExamenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialExamenesScreen(
    viewModel: ExamenViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)

    // Filtrar la lista en tiempo real según el texto ingresado
    val examenesFiltrados = uiState.listaExamenes.filter {
        it.pacienteNombre.contains(uiState.filtroTexto, ignoreCase = true) ||
                it.rut.contains(uiState.filtroTexto, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Exámenes", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulHeader)
            )
        },
        bottomBar = {
            Button(
                onClick = onVolver,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = azulHeader)
            ) {
                Text("Volver al Dashboard")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Barra de Búsqueda / Filtro
            OutlinedTextField(
                value = uiState.filtroTexto,
                onValueChange = { viewModel.onFiltroChange(it) },
                label = { Text("Buscar por Paciente o RUT...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(examenesFiltrados) { examen ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFECE6F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = examen.pacienteNombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "RUT: ${examen.rut} | Fecha: ${examen.fecha}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Examen: ${examen.tipoExamen}",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = examen.observaciones,
                                color = azulHeader,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}