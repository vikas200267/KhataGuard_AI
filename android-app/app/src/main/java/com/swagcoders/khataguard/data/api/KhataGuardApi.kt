package com.swagcoders.khataguard.data.api

import com.swagcoders.khataguard.data.model.CaseSummary
import retrofit2.http.GET

interface KhataGuardApi {
    @GET("health")
    suspend fun health(): Map<String, String>

    @GET("api/v1/states")
    suspend fun states(): List<String>

    @GET("api/v1/document-types")
    suspend fun documentTypes(): List<Map<String, String>>
}
