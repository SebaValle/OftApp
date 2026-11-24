package com.example.oftapp.data.remote

import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LoincApiService {

    @GET("api/loinc_items/v3/search")
    suspend fun buscarCodigosLoinc(
        @Query("terms") termino: String,
        @Query("maxList") limite: Int = 5
    ): Response<JsonArray>
}