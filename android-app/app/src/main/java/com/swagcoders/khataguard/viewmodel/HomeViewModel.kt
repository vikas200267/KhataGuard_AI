package com.swagcoders.khataguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swagcoders.khataguard.data.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: CaseRepository) : ViewModel() {
    private val _status = MutableStateFlow("idle")
    val status: StateFlow<String> = _status

    fun checkApi() {
        viewModelScope.launch {
            runCatching { repository.health() }
                .onSuccess { _status.value = it }
                .onFailure { _status.value = "error" }
        }
    }
}
