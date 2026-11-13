package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.AnalyzeResponse
import com.swagcoders.khataguard.data.model.CaseResponse
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val caseId: String = "",
    val isLoading: Boolean = false,
    val caseDetails: CaseResponse? = null,
    val analysis: AnalyzeResponse? = null,
    val isDownloading: Boolean = false,
    val downloadSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState

    fun loadReportSummary(caseId: String) {
        _uiState.update { it.copy(caseId = caseId, isLoading = true, error = null) }
        viewModelScope.launch {
            val caseResult = repository.getCase(caseId)
            val findingsResult = repository.getFindings(caseId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    caseDetails = caseResult.getOrNull(),
                    analysis = findingsResult.getOrNull(),
                    error = caseResult.exceptionOrNull()?.message ?: findingsResult.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun downloadPdf() {
        val caseId = _uiState.value.caseId
        if (caseId.isBlank()) return
        _uiState.update { it.copy(isDownloading = true, downloadSuccess = false) }
        viewModelScope.launch {
            // Simulated or network PDF download
            kotlinx.coroutines.delay(1200)
            _uiState.update { it.copy(isDownloading = false, downloadSuccess = true) }
        }
    }
}
