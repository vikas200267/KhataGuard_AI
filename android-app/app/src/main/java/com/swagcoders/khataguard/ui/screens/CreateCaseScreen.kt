package com.swagcoders.khataguard.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swagcoders.khataguard.viewmodel.CreateCaseViewModel

@Composable
fun CreateCaseScreen(
    onCreated: (String) -> Unit,
    viewModel: CreateCaseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdCaseId) {
        uiState.createdCaseId?.let(onCreated)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Create Property Case", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(
                    value = uiState.propertyName,
                    onValueChange = viewModel::onPropertyNameChange,
                    label = { Text("Property name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Select state")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.availableStates.forEach { state ->
                        OutlinedButton(
                            onClick = { viewModel.onStateSelect(state) },
                            enabled = !uiState.isSubmitting,
                        ) {
                            val selected = if (state == uiState.selectedState) "✓ " else ""
                            Text("$selected$state")
                        }
                    }
                }
                uiState.error?.let { Text("Error: $it") }
                Button(
                    onClick = viewModel::createCase,
                    enabled = !uiState.isSubmitting,
                ) {
                    Text(if (uiState.isSubmitting) "Creating..." else "Create Case")
                }
            }
        }
    }
}
