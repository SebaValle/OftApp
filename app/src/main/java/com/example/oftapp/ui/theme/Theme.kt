package com.example.oftapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AzulMedico,
    secondary = VerdeTeal,
    background = FondoBlancoHumo,
    onBackground = TextoGrisOscuro
)

@Composable
fun OftAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}