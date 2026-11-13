package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.AnalyzeResponse
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProcessingUiState(
    val caseId: String = "",
    val currentStep: Int = 1,
    val currentStepName: String = "Initializing AI Agents...",
    val progress: Float = 0.1f,
    val isComplete: Boolean = false,
    val error: String? = null,
    val analysisResult: AnalyzeResponse? = null,
)

@HiltViewModel
class ProcessingViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProcessingUiState())
    val uiState: StateFlow<ProcessingUiState> = _uiState

    fun startProcessing(caseId: String) {
        _uiState.update { it.copy(caseId = caseId, isComplete = false, error = null, progress = 0.1f) }
        viewModelScope.launch {
            try {
                // Step 1: Multimodal OCR & Document Understanding
                _uiState.update { it.copy(currentStep = 1, currentStepName = "Multimodal Document Ingestion & OCR", progress = 0.25f) }
                delay(800)

                // Step 2: Normalization Tool
                _uiState.update { it.copy(currentStep = 2, currentStepName = "Normalizing Names, Dates & Land Extents", progress = 0.50f) }
                delay(700)

                // Step 3: State Adapter & Consistency Engine
                _uiState.update { it.copy(currentStep = 3, currentStepName = "Executing State-Specific Consistency Rules", progress = 0.75f) }
                delay(700)

                // Step 4: Building Property Identity Graph
                _uiState.update { it.copy(currentStep = 4, currentStepName = "Constructing Property Identity Graph", progress = 0.90f) }
                val result = repository.analyzeCase(caseId).getOrThrow()

                // Complete
                _uiState.update {
                    it.copy(
                        currentStep = 5,
                        currentStepName = "Analysis Complete! Findings Generated.",
                        progress = 1.0f,
                        isComplete = true,
                        analysisResult = result,
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed during document processing") }
            }
        }
    }
}
