package com.example.oftapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HapiFhirApiService {

    @GET("DiagnosticReport")
    suspend fun buscarReportesOftalmologicos(
        @Query("_count") limite: Int = 5,
        @Query("_format") formato: String = "json"
    ): Response<FhirBundleResponse>
}