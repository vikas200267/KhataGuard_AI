package com.swagcoders.khataguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DemoModeBanner() {
    Text(
        text = "DEMO MODE — synthetic data only",
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E88E5))
            .padding(8.dp),
    )
}

@Composable
fun DocumentCard(title: String, subtitle: String) {
    Card {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun StatusIndicator(status: String) = Text("Status: $status")

@Composable
fun DiscrepancyBadge(label: String) = Text("⚠ $label", color = Color(0xFF2E7D32))

@Composable
fun EvidenceReference(text: String) = Text("Evidence: $text", style = MaterialTheme.typography.bodySmall)

@Composable
fun LoadingState(message: String) = Text("Loading: $message")

@Composable
fun ErrorMessage(message: String) = Text("Error: $message", color = Color.Red)
