package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.DocumentRecord
import com.swagcoders.khataguard.data.model.DocumentTypeItem
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UploadUiState(
    val caseId: String = "",
    val documents: List<DocumentRecord> = emptyList(),
    val availableDocTypes: List<DocumentTypeItem> = emptyList(),
    val isUploading: Boolean = false,
    val selectedDocType: String = "sale_deed",
    val error: String? = null,
)

@HiltViewModel
class UploadViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UploadUiState())
    val uiState: StateFlow<UploadUiState> = _uiState

    fun initCase(caseId: String) {
        _uiState.update { it.copy(caseId = caseId) }
        viewModelScope.launch {
            val caseResult = repository.getCase(caseId)
            val docTypesResult = repository.getDocumentTypes()
            _uiState.update {
                it.copy(
                    documents = caseResult.getOrNull()?.documents ?: emptyList(),
                    availableDocTypes = docTypesResult.getOrNull() ?: emptyList(),
                )
            }
        }
    }

    fun selectDocType(type: String) {
        _uiState.update { it.copy(selectedDocType = type) }
    }

    fun addDemoDocument(filename: String, docType: String) {
        val caseId = _uiState.value.caseId
        if (caseId.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, error = null) }
            val doc = repository.addDemoDocument(caseId, filename, docType)
            _uiState.update {
                it.copy(
                    isUploading = false,
                    documents = it.documents + doc,
                )
            }
        }
    }
}
