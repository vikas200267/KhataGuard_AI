package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.Finding
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscrepancyUiState(
    val caseId: String = "",
    val isLoading: Boolean = false,
    val findings: List<Finding> = emptyList(),
    val selectedFinding: Finding? = null,
    val error: String? = null,
)

@HiltViewModel
class DiscrepancyViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DiscrepancyUiState())
    val uiState: StateFlow<DiscrepancyUiState> = _uiState

    fun loadFindings(caseId: String) {
        _uiState.update { it.copy(caseId = caseId, isLoading = true, error = null) }
        viewModelScope.launch {
            val result = repository.getFindings(caseId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    findings = result.getOrNull()?.findings ?: emptyList(),
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }

    fun selectFinding(finding: Finding?) {
        _uiState.update { it.copy(selectedFinding = finding) }
    }
}
