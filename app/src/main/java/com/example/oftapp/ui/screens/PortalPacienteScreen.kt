package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.viewmodel.ExamenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortalPacienteScreen(
    viewModel: ExamenViewModel,
    onVerMisExamenes: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val azulHeader = Color(0xFF0066B2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OftApp - Portal del Paciente", color = Color.White) },
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
            // Header del Paciente estilo mockup
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido/a",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Pedro Gómez",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = azulHeader
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "RUT: 11.111.111-1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Text(
                text = "Tus Opciones",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = azulHeader,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Botón de acceso a Mis Exámenes
            Card(
                onClick = onVerMisExamenes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📋", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Consultar Mis Exámenes", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Revisa tus atenciones y resultados", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    Text("›", fontSize = 24.sp, color = Color.Gray)
                }
            }

            // Muestra de parámetros oftalmológicos en pantalla según el mockup
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Último Examen Validado: Campimetría / OD",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = azulHeader
                    )
                    Text(
                        text = "Fecha: 27/10/2026",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))

                    Text("Resultados Clínicos Oftalmológicos", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    ParametroFila("Agudeza Visual OD", "20/20")
                    ParametroFila("Presión Intraocular (PIO)", "14 mmHg")
                    ParametroFila("Campo Visual Superior", "Normal")
                    ParametroFila("Sensibilidad al Contraste", "Normal (100%)")

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✔", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Examen firmado y validado por el médico tratante.",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Nota de privacidad de datos
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔒", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tu información es privada y solo puedes visualizar los registros asociados a tu RUT.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = onVolver) {
                Text("Volver al Inicio", color = Color.Gray)
            }
        }
    }
}

@Composable
fun ParametroFila(nombre: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = nombre, fontSize = 12.sp, color = Color.DarkGray)
        Text(text = valor, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}