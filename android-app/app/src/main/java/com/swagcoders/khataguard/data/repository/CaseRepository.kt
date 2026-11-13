package com.swagcoders.khataguard.data.repository

import com.swagcoders.khataguard.data.api.KhataGuardApi
import javax.inject.Inject

class CaseRepository @Inject constructor(private val api: KhataGuardApi) {
    suspend fun health() = api.health()["status"] ?: "unknown"
}
