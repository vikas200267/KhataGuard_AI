package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.ExtractionOutput
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailsUiState(
    val caseId: String = "",
    val isLoading: Boolean = false,
    val extractions: List<ExtractionOutput> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState

    fun loadDetails(caseId: String) {
        _uiState.update { it.copy(caseId = caseId, isLoading = true, error = null) }
        viewModelScope.launch {
            val result = repository.getFindings(caseId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    extractions = result.getOrNull()?.extractedDocuments ?: emptyList(),
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
