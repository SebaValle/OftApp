package com.example.oftapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface InsForgeApiService {

    // --- EXÁMENES (InsForge REST API) ---
    @Headers("Prefer: return=representation")
    @POST("api/database/records/examenes")
    suspend fun subirExamen(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body examenes: List<InsForgeExamenDto>
    ): Response<List<InsForgeExamenDto>>

    @GET("api/database/records/examenes?select=*&order=id.desc")
    suspend fun obtenerExamenesRemotos(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String
    ): Response<List<InsForgeExamenDto>>

    // --- USUARIOS (InsForge REST API) ---
    @GET("api/database/records/usuarios?select=*")
    suspend fun validarUsuarioRemoto(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("username") filtroUsername: String,
        @Query("password") filtroPassword: String
    ): Response<List<InsForgeUsuarioDto>>

    @Headers("Prefer: return=representation")
    @POST("api/database/records/usuarios")
    suspend fun crearUsuarioRemoto(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Body usuarios: List<InsForgeUsuarioDto>
    ): Response<List<InsForgeUsuarioDto>>

    @PATCH("api/database/records/usuarios")
    suspend fun actualizarPasswordRemota(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("username") filtroUsername: String,
        @Body campos: Map<String, String>
    ): Response<Unit>
}