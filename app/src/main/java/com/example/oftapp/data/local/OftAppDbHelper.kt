package com.example.oftapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.oftapp.data.local.entity.ExamenEntity

class OftAppDbHelper(context: Context) : SQLiteOpenHelper(context, "oftapp_local.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE examenes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pacienteNombre TEXT NOT NULL,
                rut TEXT NOT NULL,
                tipoExamen TEXT NOT NULL,
                fecha TEXT NOT NULL,
                observaciones TEXT NOT NULL,
                documentoUri TEXT,
                sincronizado INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS examenes")
        onCreate(db)
    }

    fun insertarExamen(examen: ExamenEntity): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("pacienteNombre", examen.pacienteNombre)
            put("rut", examen.rut)
            put("tipoExamen", examen.tipoExamen)
            put("fecha", examen.fecha)
            put("observaciones", examen.observaciones)
            put("documentoUri", examen.documentoUri)
            put("sincronizado", if (examen.sincronizado) 1 else 0)
        }
        return db.insert("examenes", null, values)
    }

    fun obtenerTodosLosExamenes(): List<ExamenEntity> {
        val lista = mutableListOf<ExamenEntity>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM examenes ORDER BY id DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    ExamenEntity(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        pacienteNombre = it.getString(it.getColumnIndexOrThrow("pacienteNombre")),
                        rut = it.getString(it.getColumnIndexOrThrow("rut")),
                        tipoExamen = it.getString(it.getColumnIndexOrThrow("tipoExamen")),
                        fecha = it.getString(it.getColumnIndexOrThrow("fecha")),
                        observaciones = it.getString(it.getColumnIndexOrThrow("observaciones")),
                        documentoUri = it.getString(it.getColumnIndexOrThrow("documentoUri")),
                        sincronizado = it.getInt(it.getColumnIndexOrThrow("sincronizado")) == 1
                    )
                )
            }
        }
        return lista
    }

    fun validarExamen(idExamen: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("observaciones", "VALIDADO POR MÉDICO")
            put("sincronizado", 0)
        }
        db.update("examenes", values, "id = ?", arrayOf(idExamen.toString()))
    }

    fun marcarComoSincronizado(idExamen: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("sincronizado", 1)
        }
        db.update("examenes", values, "id = ?", arrayOf(idExamen.toString()))
    }
}