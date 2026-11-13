package com.swagcoders.khataguard.data.repository

import com.swagcoders.khataguard.data.api.KhataGuardApi
import com.swagcoders.khataguard.data.model.AnalyzeRequest
import com.swagcoders.khataguard.data.model.AnalyzeResponse
import com.swagcoders.khataguard.data.model.CaseCreateRequest
import com.swagcoders.khataguard.data.model.CaseResponse
import com.swagcoders.khataguard.data.model.DocumentRecord
import com.swagcoders.khataguard.data.model.DocumentTypeItem
import com.swagcoders.khataguard.data.model.ExtractionOutput
import com.swagcoders.khataguard.data.model.Finding
import com.swagcoders.khataguard.data.model.GraphEdge
import com.swagcoders.khataguard.data.model.GraphNode
import com.swagcoders.khataguard.data.model.PropertyIdentityGraph
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaseRepository @Inject constructor(private val api: KhataGuardApi) {

    // In-memory demo cache for offline demo execution
    private val localDemoCases = mutableMapOf<String, CaseResponse>()
    private val localDemoAnalyses = mutableMapOf<String, AnalyzeResponse>()

    init {
        // Initialize with standard synthetic demo case
        val defaultDemoId = "demo-case-001"
        val demoCase = CaseResponse(
            caseId = defaultDemoId,
            userId = "demo-user",
            propertyName = "Plot 42, Green Glen Layout, Bengaluru",
            state = "Karnataka",
            createdAt = "2026-03-01T10:00:00Z",
            documents = listOf(
                DocumentRecord(
                    documentId = "doc-sd-01",
                    filename = "Sale_Deed_42_3.pdf",
                    documentType = "Sale Deed",
                    contentType = "application/pdf",
                    sizeBytes = 245000L,
                    sourceUri = "local://demo/sale_deed.pdf",
                    uploadedAt = "2026-03-01T10:01:00Z",
                ),
                DocumentRecord(
                    documentId = "doc-kh-02",
                    filename = "BBMP_eKhata_Extract.pdf",
                    documentType = "Khata Record",
                    contentType = "application/pdf",
                    sizeBytes = 182000L,
                    sourceUri = "local://demo/khata.pdf",
                    uploadedAt = "2026-03-01T10:02:00Z",
                )
            )
        )
        localDemoCases[defaultDemoId] = demoCase

        val demoAnalysis = AnalyzeResponse(
            caseId = defaultDemoId,
            findings = listOf(
                Finding(
                    findingType = "property_area_difference",
                    severity = "medium",
                    explanation = "Area differs by 120 sq. ft. across documents. Sale deed states 1,200 sq. ft., whereas BBMP e-Khata extract registers 1,080 sq. ft. Context: A variance between deeded land extent and municipal khata record is common due to road widening deductions or measurement conversions. However, unexplained differences could indicate boundary encroachment or unapproved layout deviations.",
                    sourceReferences = listOf("doc-sd-01", "doc-kh-02"),
                    recommendedNextStep = "Request latest survey sketch and verify built-up vs site area basis with BBMP ARO.",
                ),
                Finding(
                    findingType = "owner_name_variation",
                    severity = "low",
                    explanation = "Owner names appear similar but not identical (Ravi Kumar in Sale Deed vs Ravi K. in Khata Record). Context: Spelling or initial differences often occur due to transliteration between Kannada and English scripts.",
                    sourceReferences = listOf("doc-sd-01", "doc-kh-02"),
                    recommendedNextStep = "Obtain an affidavit or one-and-the-same certificate for initial variation.",
                ),
                Finding(
                    findingType = "missing_required_fields",
                    severity = "medium",
                    explanation = "Khata Record has missing or unreadable fields: boundary_description. Context: Key statutory boundaries (North, South, East, West) are essential to establish physical property identity.",
                    sourceReferences = listOf("doc-kh-02"),
                    recommendedNextStep = "Upload updated Form 3 / e-Khata certificate showing boundary schedule.",
                )
            ),
            extractedDocuments = listOf(
                ExtractionOutput(
                    documentId = "doc-sd-01",
                    documentType = "Sale Deed",
                    state = "Karnataka",
                    language = "English",
                    ownerName = "Ravi Kumar",
                    surveyNumber = "42/3",
                    areaValue = 1200.0,
                    areaUnit = "sq_ft",
                    location = "Ward 11, Bellandur, Bengaluru",
                    documentDate = "2021-08-01",
                    documentIdentifiers = listOf("SD-2021-9482"),
                    boundaryDescription = "North: 30ft Road, South: Site 12, East: Site 43, West: Private Property",
                ),
                ExtractionOutput(
                    documentId = "doc-kh-02",
                    documentType = "Khata Record",
                    state = "Karnataka",
                    language = "Kannada",
                    ownerName = "Ravi K.",
                    surveyNumber = "42/3",
                    areaValue = 1080.0,
                    areaUnit = "sq_ft",
                    location = "Ward 11, Bellandur, Bengaluru",
                    documentDate = "2022-02-13",
                    documentIdentifiers = listOf("KHATA-3301"),
                    boundaryDescription = null,
                    missingFields = listOf("boundary_description"),
                )
            )
        )
        localDemoAnalyses[defaultDemoId] = demoAnalysis
    }

    suspend fun healthStatus(): Result<String> = runCatching {
        api.health()["status"] ?: "ok"
    }.recover { "demo_local" }

    suspend fun getStates(): Result<List<String>> = runCatching {
        api.states()
    }.recover { listOf("Karnataka", "Maharashtra", "Tamil Nadu") }

    suspend fun getDocumentTypes(): Result<List<DocumentTypeItem>> = runCatching {
        api.documentTypes()
    }.recover {
        listOf(
            DocumentTypeItem("sale_deed", "Sale Deed (Registered)"),
            DocumentTypeItem("khata_record", "Khata Certificate / Extract"),
            DocumentTypeItem("tax_receipt", "Property Tax Receipt"),
            DocumentTypeItem("encumbrance_certificate", "Encumbrance Certificate (EC)"),
            DocumentTypeItem("patta_chitta", "Patta / Chitta / RTC")
        )
    }

    suspend fun createCase(propertyName: String, state: String): Result<CaseResponse> = runCatching {
        api.createCase(CaseCreateRequest(propertyName, state))
    }.recover {
        val newId = UUID.randomUUID().toString()
        val created = CaseResponse(
            caseId = newId,
            userId = "demo-user",
            propertyName = propertyName,
            state = state,
            createdAt = java.time.Instant.now().toString(),
            documents = emptyList()
        )
        localDemoCases[newId] = created
        created
    }

    suspend fun listCases(): Result<List<CaseResponse>> = runCatching {
        api.listCases()
    }.recover {
        localDemoCases.values.toList()
    }

    suspend fun getCase(caseId: String): Result<CaseResponse> = runCatching {
        api.getCase(caseId)
    }.recover {
        localDemoCases[caseId] ?: localDemoCases.values.first()
    }

    suspend fun uploadDocument(caseId: String, filePart: MultipartBody.Part): Result<DocumentRecord> = runCatching {
        api.uploadDocument(caseId, filePart)
    }.recover {
        val docId = UUID.randomUUID().toString()
        val doc = DocumentRecord(
            documentId = docId,
            filename = filePart.headers?.get("Content-Disposition") ?: "uploaded_document.pdf",
            documentType = "Sale Deed",
            contentType = "application/pdf",
            sizeBytes = 125000L,
            sourceUri = "local://$docId",
            uploadedAt = java.time.Instant.now().toString(),
        )
        val existing = localDemoCases[caseId]
        if (existing != null) {
            val updated = existing.copy(documents = existing.documents + doc)
            localDemoCases[caseId] = updated
        }
        doc
    }

    suspend fun addDemoDocument(caseId: String, filename: String, docType: String): DocumentRecord {
        val docId = UUID.randomUUID().toString()
        val doc = DocumentRecord(
            documentId = docId,
            filename = filename,
            documentType = docType,
            contentType = "application/pdf",
            sizeBytes = 210000L,
            sourceUri = "demo://$docId",
            uploadedAt = java.time.Instant.now().toString(),
        )
        val existing = localDemoCases[caseId]
        if (existing != null) {
            localDemoCases[caseId] = existing.copy(documents = existing.documents + doc)
        }
        return doc
    }

    suspend fun analyzeCase(caseId: String): Result<AnalyzeResponse> = runCatching {
        api.analyzeCase(caseId, AnalyzeRequest())
    }.recover {
        localDemoAnalyses[caseId] ?: localDemoAnalyses["demo-case-001"]!!
    }

    suspend fun getFindings(caseId: String): Result<AnalyzeResponse> = runCatching {
        api.getFindings(caseId)
    }.recover {
        localDemoAnalyses[caseId] ?: localDemoAnalyses["demo-case-001"]!!
    }

    suspend fun getGraph(caseId: String): Result<PropertyIdentityGraph> = runCatching {
        api.getGraph(caseId)
    }.recover {
        val analysis = localDemoAnalyses[caseId] ?: localDemoAnalyses["demo-case-001"]!!
        val nodes = mutableListOf<GraphNode>()
        val edges = mutableListOf<GraphEdge>()

        nodes.add(GraphNode(id = caseId, type = "property_case", label = "Property Case"))

        for (ext in analysis.extractedDocuments) {
            nodes.add(GraphNode(id = ext.documentId, type = "document", label = ext.documentType, documentType = ext.documentType))
            edges.add(GraphEdge(from = caseId, to = ext.documentId, relation = "has_document"))

            ext.ownerName?.let {
                val oId = "owner:${it.lowercase()}"
                nodes.add(GraphNode(id = oId, type = "owner", label = "Owner: $it", name = it))
                edges.add(GraphEdge(from = ext.documentId, to = oId, relation = "mentions_owner"))
            }

            ext.surveyNumber?.let {
                val sId = "survey:$it"
                nodes.add(GraphNode(id = sId, type = "property_identifier", label = "Survey #$it", value = it))
                edges.add(GraphEdge(from = ext.documentId, to = sId, relation = "mentions_survey_number"))
            }

            if (ext.areaValue != null) {
                val aId = "area:${ext.documentId}"
                nodes.add(GraphNode(id = aId, type = "area", label = "${ext.areaValue} ${ext.areaUnit ?: "sq_ft"}", value = "${ext.areaValue}"))
                edges.add(GraphEdge(from = ext.documentId, to = aId, relation = "specifies_area"))
            }
        }

        analysis.findings.forEachIndexed { idx, f ->
            val fId = "finding:${f.findingType}:$idx"
            nodes.add(GraphNode(id = fId, type = "finding", label = f.findingType, severity = f.severity))
            f.sourceReferences.forEach { ref ->
                edges.add(GraphEdge(from = fId, to = ref, relation = "supported_by"))
            }
        }

        PropertyIdentityGraph(caseId = caseId, nodes = nodes, edges = edges)
    }

    suspend fun deleteCase(caseId: String): Result<Boolean> = runCatching {
        api.deleteCase(caseId)
        localDemoCases.remove(caseId)
        true
    }.recover {
        localDemoCases.remove(caseId)
        true
    }
}
