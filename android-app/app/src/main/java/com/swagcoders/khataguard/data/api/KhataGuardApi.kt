package com.swagcoders.khataguard.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface KhataGuardApi {
    @GET("health")
    suspend fun health(): Map<String, String>

    @GET("api/v1/states")
    suspend fun states(): List<String>

    @GET("api/v1/document-types")
    suspend fun documentTypes(): List<Map<String, String>>

    @POST("api/v1/cases")
    suspend fun createCase(@Body payload: Map<String, String>): Map<String, Any>
}
