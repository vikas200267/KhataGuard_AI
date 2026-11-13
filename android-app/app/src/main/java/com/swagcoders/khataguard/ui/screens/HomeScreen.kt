package com.swagcoders.khataguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swagcoders.khataguard.ui.components.DemoModeBanner
import com.swagcoders.khataguard.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onCreateCase: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DemoModeBanner()
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Home Dashboard", style = MaterialTheme.typography.headlineSmall)
                Text("Backend status: ${uiState.apiStatus}")
                Text("Supported states: ${if (uiState.states.isEmpty()) "loading..." else uiState.states.joinToString()}")
                uiState.error?.let { Text("Error: $it") }
            }
        }
        Button(onClick = onCreateCase, enabled = !uiState.isLoading) {
            Text("Create Property Case")
        }
    }
}
