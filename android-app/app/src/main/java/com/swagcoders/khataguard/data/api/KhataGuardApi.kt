package com.swagcoders.khataguard.data.api

import com.swagcoders.khataguard.data.model.AnalyzeRequest
import com.swagcoders.khataguard.data.model.AnalyzeResponse
import com.swagcoders.khataguard.data.model.CaseCreateRequest
import com.swagcoders.khataguard.data.model.CaseResponse
import com.swagcoders.khataguard.data.model.DocumentRecord
import com.swagcoders.khataguard.data.model.DocumentTypeItem
import com.swagcoders.khataguard.data.model.PropertyIdentityGraph
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface KhataGuardApi {
    @GET("health")
    suspend fun health(): Map<String, String>

    @GET("api/v1/states")
    suspend fun states(): List<String>

    @GET("api/v1/document-types")
    suspend fun documentTypes(): List<DocumentTypeItem>

    @POST("api/v1/cases")
    suspend fun createCase(@Body payload: CaseCreateRequest): CaseResponse

    @GET("api/v1/cases")
    suspend fun listCases(): List<CaseResponse>

    @GET("api/v1/cases/{caseId}")
    suspend fun getCase(@Path("caseId") caseId: String): CaseResponse

    @Multipart
    @POST("api/v1/cases/{caseId}/documents")
    suspend fun uploadDocument(
        @Path("caseId") caseId: String,
        @Part file: MultipartBody.Part,
    ): DocumentRecord

    @POST("api/v1/cases/{caseId}/analyze")
    suspend fun analyzeCase(
        @Path("caseId") caseId: String,
        @Body payload: AnalyzeRequest = AnalyzeRequest(),
    ): AnalyzeResponse

    @GET("api/v1/cases/{caseId}/findings")
    suspend fun getFindings(@Path("caseId") caseId: String): AnalyzeResponse

    @GET("api/v1/cases/{caseId}/graph")
    suspend fun getGraph(@Path("caseId") caseId: String): PropertyIdentityGraph

    @GET("api/v1/cases/{caseId}/report")
    suspend fun getReport(@Path("caseId") caseId: String): ResponseBody

    @DELETE("api/v1/cases/{caseId}")
    suspend fun deleteCase(@Path("caseId") caseId: String): Map<String, String>
}
