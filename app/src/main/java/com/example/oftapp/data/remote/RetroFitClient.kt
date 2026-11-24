package com.example.oftapp.data.remote

import com.example.oftapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Credenciales cargadas de forma segura desde local.properties via BuildConfig
    val INSFORGE_BASE_URL: String = BuildConfig.INSFORGE_BASE_URL
    val INSFORGE_ANON_KEY: String = BuildConfig.INSFORGE_ANON_KEY

    private const val HAPI_FHIR_BASE_URL = "https://hapi.fhir.org/baseR4/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    val insForgeService: InsForgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(INSFORGE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InsForgeApiService::class.java)
    }

    val fhirService: HapiFhirApiService by lazy {
        Retrofit.Builder()
            .baseUrl(HAPI_FHIR_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HapiFhirApiService::class.java)
    }
}