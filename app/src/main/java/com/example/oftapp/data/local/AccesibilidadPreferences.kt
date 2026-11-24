package com.example.oftapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "oftapp_accesibilidad")

class AccesibilidadPreferences(private val context: Context) {

    companion object {
        val ESCALA_TEXTO_KEY = floatPreferencesKey("escala_texto")
        val ALTO_CONTRASTE_KEY = booleanPreferencesKey("alto_contraste")
    }

    val escalaTextoFlow: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[ESCALA_TEXTO_KEY] ?: 1.0f
    }

    val altoContrasteFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ALTO_CONTRASTE_KEY] ?: false
    }

    suspend fun guardarEscalaTexto(escala: Float) {
        context.dataStore.edit { prefs ->
            prefs[ESCALA_TEXTO_KEY] = escala
        }
    }

    suspend fun guardarAltoContraste(activado: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ALTO_CONTRASTE_KEY] = activado
        }
    }
}