package com.swagcoders.khataguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swagcoders.khataguard.ui.components.AccentGreen
import com.swagcoders.khataguard.ui.components.DemoModeBanner
import com.swagcoders.khataguard.ui.components.DisclaimerBox
import com.swagcoders.khataguard.ui.components.PrimaryBlue

@Composable
fun StateSelectionScreen(
    onSelectState: (String) -> Unit,
    onBack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        DemoModeBanner()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "State Jurisdiction Adapters",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
            Text(
                text = "Each state in India has unique land terminology, registration portals, and revenue documents. Select a state to inspect supported rules.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
            )

            // Karnataka Card
            StateCard(
                stateName = "Karnataka",
                portalName = "Kaveri 2.0 • Bhoomi • e-Swathu",
                status = "VALIDATED v1",
                statusColor = AccentGreen,
                description = "Full statutory rules for Sale Deeds, RTC (Pahani), and BBMP e-Khata extract. Deterministic area discrepancy detection enabled.",
                onClick = { onSelectState("Karnataka") },
            )

            // Maharashtra Card
            StateCard(
                stateName = "Maharashtra",
                portalName = "IGR Maharashtra • Mahabhulekh",
                status = "PLACEHOLDER v1",
                statusColor = Color(0xFFE65100),
                description = "General extraction for Sale Deed & 7/12 (Satbara Utara) extract. Tenancy and NA conversion checks require official verification.",
                onClick = { onSelectState("Maharashtra") },
            )

            // Tamil Nadu Card
            StateCard(
                stateName = "Tamil Nadu",
                portalName = "TNREGINET • e-Services",
                status = "PLACEHOLDER v1",
                statusColor = Color(0xFFE65100),
                description = "General extraction for Sale Deed & Patta/Chitta. Temple land and conditional assignment checks require official clearance.",
                onClick = { onSelectState("Tamil Nadu") },
            )

            DisclaimerBox()
        }

        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Back", color = PrimaryBlue)
            }
        }
    }
}

@Composable
private fun StateCard(
    stateName: String,
    portalName: String,
    status: String,
    statusColor: Color,
    description: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stateName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Surface(color = statusColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                    Text(
                        text = status,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }
            Text(portalName, style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
        }
    }
}
