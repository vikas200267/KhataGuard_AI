package com.swagcoders.khataguard.data.model

data class CaseSummary(
    val caseId: String,
    val propertyName: String,
    val state: String,
)

data class FindingUi(
    val type: String,
    val severity: String,
    val explanation: String,
)
