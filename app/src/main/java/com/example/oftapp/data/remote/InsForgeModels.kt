package com.example.oftapp.data.remote

import com.google.gson.annotations.SerializedName

// DTO para enviar/recibir registros en la tabla 'examenes' de InsForge
data class InsForgeExamenDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("paciente_nombre") val pacienteNombre: String,
    @SerializedName("rut") val rut: String,
    @SerializedName("tipo_examen") val tipoExamen: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("observaciones") val observaciones: String,
    @SerializedName("documento_uri") val documentoUri: String? = null
)

// DTO para validar/crear registros en la tabla 'usuarios' de InsForge
data class InsForgeUsuarioDto(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("rut") val rut: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean = true
)

// DTO mínimo para consultar reportes clínicos en HAPI FHIR R4
data class FhirBundleResponse(
    @SerializedName("resourceType") val resourceType: String?,
    @SerializedName("total") val total: Int?,
    @SerializedName("entry") val entry: List<FhirEntry>?
)

data class FhirEntry(
    @SerializedName("resource") val resource: FhirDiagnosticReport?
)

data class FhirDiagnosticReport(
    @SerializedName("id") val id: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("code") val code: FhirCode?
)

data class FhirCode(
    @SerializedName("text") val text: String?
)