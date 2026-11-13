package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.model.PropertyIdentityGraph
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GraphUiState(
    val caseId: String = "",
    val isLoading: Boolean = false,
    val graph: PropertyIdentityGraph? = null,
    val error: String? = null,
)

@HiltViewModel
class GraphViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GraphUiState())
    val uiState: StateFlow<GraphUiState> = _uiState

    fun loadGraph(caseId: String) {
        _uiState.update { it.copy(caseId = caseId, isLoading = true, error = null) }
        viewModelScope.launch {
            val result = repository.getGraph(caseId)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    graph = result.getOrNull(),
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
