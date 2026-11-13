package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateCaseUiState(
    val propertyName: String = "",
    val selectedState: String = "Karnataka",
    val availableStates: List<String> = listOf("Karnataka", "Maharashtra", "Tamil Nadu"),
    val createdCaseId: String? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CreateCaseViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateCaseUiState())
    val uiState: StateFlow<CreateCaseUiState> = _uiState

    fun onPropertyNameChange(value: String) {
        _uiState.update { it.copy(propertyName = value) }
    }

    fun onStateSelect(value: String) {
        _uiState.update { it.copy(selectedState = value) }
    }

    fun createCase() {
        val current = _uiState.value
        if (current.propertyName.length < 2) {
            _uiState.update { it.copy(error = "Property name must be at least 2 characters") }
            return
        }
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val result = repository.createCase(current.propertyName, current.selectedState)
            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    createdCaseId = result.getOrNull()?.caseId,
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
