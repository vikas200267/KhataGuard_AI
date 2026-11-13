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

data class HomeUiState(
    val isLoading: Boolean = false,
    val apiStatus: String = "idle",
    val states: List<String> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadDashboard() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val statusResult = repository.healthStatus()
            val statesResult = repository.getStates()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    apiStatus = statusResult.getOrElse { "error" },
                    states = statesResult.getOrElse { emptyList() },
                    error = statusResult.exceptionOrNull()?.message ?: statesResult.exceptionOrNull()?.message,
                )
            }
        }
    }
}
