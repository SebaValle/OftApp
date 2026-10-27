package com.example.oftapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oftapp.data.local.entity.ExamenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamenDao {
    @Query("SELECT * FROM examenes ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ExamenEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun meInsertar(examen: ExamenEntity)

    @Delete
    suspend fun eliminar(examen: ExamenEntity)
}