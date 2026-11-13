package com.swagcoders.khataguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swagcoders.khataguard.ui.components.DemoModeBanner
import com.swagcoders.khataguard.ui.components.DisclaimerBox
import com.swagcoders.khataguard.ui.components.HighSeverityRed
import com.swagcoders.khataguard.ui.components.MedSeverityOrange
import com.swagcoders.khataguard.ui.components.PrimaryBlue

@Composable
fun EvidenceViewerScreen(
    caseId: String,
    onBackToDiscrepancies: () -> Unit,
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
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Side-by-Side Evidence Viewer",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
            Text(
                text = "Comparing cited source citations that triggered the 120 sq. ft. area variance finding.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
            )

            // Primary Mismatch Highlight Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Detected Area Variance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MedSeverityOrange,
                        )
                        Surface(color = MedSeverityOrange, shape = RoundedCornerShape(4.dp)) {
                            Text("Δ 120 sq. ft.", color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Text(
                        text = "Sale Deed registers 1,200 sq. ft., whereas municipal BBMP e-Khata records 1,080 sq. ft.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7C2D12),
                    )
                }
            }

            // Side by Side Comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Document A
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Doc A: Sale Deed", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Page 1, Schedule A", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Divider()
                        Text("Owner:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("Ravi Kumar", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Survey No:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("42/3", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Area:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("1,200 sq. ft.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }

                // Document B
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Doc B: Khata Extract", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF0D9488))
                        Text("Page 1, Assessment Reg", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Divider()
                        Text("Owner:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("Ravi K.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Survey No:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("42/3", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("Area:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                        Text("1,080 sq. ft.", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MedSeverityOrange)
                    }
                }
            }

            DisclaimerBox()
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = onBackToDiscrepancies,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                Text("Return to Discrepancy Findings", fontWeight = FontWeight.Bold)
            }
        }
    }
}
