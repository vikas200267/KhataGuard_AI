package com.swagcoders.khataguard.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaseSummary(
    val caseId: String,
    val propertyName: String,
    val state: String,
    val documentsCount: Int = 0,
    val createdAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class CaseCreateRequest(
    @Json(name = "property_name") val propertyName: String,
    val state: String,
)

@JsonClass(generateAdapter = true)
data class CaseResponse(
    @Json(name = "case_id") val caseId: String,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "property_name") val propertyName: String,
    val state: String,
    @Json(name = "created_at") val createdAt: String? = null,
    val documents: List<DocumentRecord> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class DocumentRecord(
    @Json(name = "document_id") val documentId: String,
    val filename: String,
    @Json(name = "document_type") val documentType: String,
    @Json(name = "content_type") val contentType: String = "",
    @Json(name = "size_bytes") val sizeBytes: Long = 0L,
    @Json(name = "source_uri") val sourceUri: String = "",
    @Json(name = "uploaded_at") val uploadedAt: String? = null,
)

@JsonClass(generateAdapter = true)
data class ExtractionOutput(
    @Json(name = "document_id") val documentId: String,
    @Json(name = "document_type") val documentType: String,
    val state: String = "",
    val language: String = "",
    @Json(name = "owner_name") val ownerName: String? = null,
    @Json(name = "survey_number") val surveyNumber: String? = null,
    @Json(name = "area_value") val areaValue: Double? = null,
    @Json(name = "area_unit") val areaUnit: String? = null,
    val location: String? = null,
    @Json(name = "document_date") val documentDate: String? = null,
    @Json(name = "document_identifiers") val documentIdentifiers: List<String> = emptyList(),
    @Json(name = "boundary_description") val boundaryDescription: String? = null,
    @Json(name = "missing_or_unreadable_fields") val missingFields: List<String> = emptyList(),
    @Json(name = "source_references") val sourceReferences: List<Map<String, Any>> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class Finding(
    @Json(name = "finding_type") val findingType: String,
    val severity: String,
    val explanation: String,
    @Json(name = "source_references") val sourceReferences: List<String> = emptyList(),
    @Json(name = "recommended_next_step") val recommendedNextStep: String = "",
)

@JsonClass(generateAdapter = true)
data class AnalyzeRequest(
    @Json(name = "transaction_type") val transactionType: String = "sale",
)

@JsonClass(generateAdapter = true)
data class AnalyzeResponse(
    @Json(name = "case_id") val caseId: String,
    val findings: List<Finding> = emptyList(),
    @Json(name = "extracted_documents") val extractedDocuments: List<ExtractionOutput> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class GraphNode(
    val id: String,
    val type: String,
    val label: String? = null,
    val name: String? = null,
    val value: String? = null,
    val severity: String? = null,
    @Json(name = "document_type") val documentType: String? = null,
    val metadata: Map<String, Any?> = emptyMap(),
)

@JsonClass(generateAdapter = true)
data class GraphEdge(
    @Json(name = "from") val from: String,
    @Json(name = "to") val to: String,
    val relation: String,
)

@JsonClass(generateAdapter = true)
data class PropertyIdentityGraph(
    @Json(name = "case_id") val caseId: String = "",
    val nodes: List<GraphNode> = emptyList(),
    val edges: List<GraphEdge> = emptyList(),
)

@JsonClass(generateAdapter = true)
data class FeeEstimateResponse(
    val status: String,
    val message: String,
    val value: Double? = null,
    val currency: String? = null,
    @Json(name = "source_url") val sourceUrl: String? = null,
)

@JsonClass(generateAdapter = true)
data class DocumentTypeItem(
    val code: String,
    val label: String,
)
