package com.swagcoders.khataguard.data.repository

import com.swagcoders.khataguard.data.api.KhataGuardApi
import com.swagcoders.khataguard.data.model.CaseSummary
import javax.inject.Inject

class CaseRepository @Inject constructor(private val api: KhataGuardApi) {
    suspend fun healthStatus(): Result<String> = runCatching { api.health()["status"] ?: "unknown" }

    suspend fun getStates(): Result<List<String>> = runCatching { api.states() }

    suspend fun createCase(propertyName: String, state: String): Result<CaseSummary> = runCatching {
        val response = api.createCase(mapOf("property_name" to propertyName, "state" to state))
        CaseSummary(
            caseId = response["case_id"]?.toString().orEmpty(),
            propertyName = response["property_name"]?.toString().orEmpty(),
            state = response["state"]?.toString().orEmpty(),
        )
    }
}
