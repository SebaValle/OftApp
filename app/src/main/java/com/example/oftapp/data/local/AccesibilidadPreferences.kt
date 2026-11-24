package com.example.oftapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "oftapp_accesibilidad")

class AccesibilidadPreferences(private val context: Context) {

    companion object {
        val ESCALA_TEXTO_KEY = floatPreferencesKey("escala_texto")
    }

    val escalaTextoFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[ESCALA_TEXTO_KEY] ?: 1.0f
    }

    suspend fun guardarEscalaTexto(nuevaEscala: Float) {
        context.dataStore.edit { preferences ->
            preferences[ESCALA_TEXTO_KEY] = nuevaEscala
        }
    }
}