package com.example.oftapp.data.local

import android.content.Context
import com.example.oftapp.data.local.dao.ExamenDao

abstract class AppDatabase {
    abstract fun examenDao(): ExamenDao?

    companion object {
        fun getDatabase(context: Context): AppDatabase? = null
    }
}