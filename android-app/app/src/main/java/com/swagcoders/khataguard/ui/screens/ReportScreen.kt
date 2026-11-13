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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swagcoders.khataguard.ui.components.AccentGreen
import com.swagcoders.khataguard.ui.components.DemoModeBanner
import com.swagcoders.khataguard.ui.components.DisclaimerBox
import com.swagcoders.khataguard.ui.components.FindingCard
import com.swagcoders.khataguard.ui.components.PrimaryBlue
import com.swagcoders.khataguard.viewmodel.ReportViewModel

@Composable
fun ReportScreen(
    caseId: String,
    onBackHome: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(caseId) {
        viewModel.loadReportSummary(caseId)
    }

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
                text = "Property Pre-Check Report",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
            )
            Text(
                text = "Autonomous preliminary document intelligence report ready for download and offline review.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
            )

            // Case Info Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Report Case: $caseId",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                    )
                    Text(
                        text = uiState.caseDetails?.propertyName ?: "Plot 42, Green Glen Layout, Bengaluru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                    )
                    Text(
                        text = "Jurisdiction: ${uiState.caseDetails?.state ?: "Karnataka"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryBlue,
                    )

                    Divider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Findings Detected:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text(
                            "${uiState.analysis?.findings?.size ?: 3} Issues Flagged",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Documents Evaluated:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        Text(
                            "${uiState.analysis?.extractedDocuments?.size ?: 2} Records",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            // Official Verification Guidance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Official Verification Checklist",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen,
                    )
                    ChecklistItem("Verify registration record on official Kaveri 2.0 portal")
                    ChecklistItem("Cross-check mutation & BBMP e-Khata extract with ARO office")
                    ChecklistItem("Validate Encumbrance Certificate Form 15 for min 30-year period")
                    ChecklistItem("Obtain Podi sketch & Tippani for survey boundary validation")
                }
            }

            // Fee Estimate Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Indicative Registration & Stamp Duty Notice",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen,
                    )
                    Text(
                        text = "Statutory stamp duty in Karnataka is calculated based on guidance value published on Kaveri online portal. Please consult your sub-registrar for certified valuation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF14532D),
                    )
                }
            }

            DisclaimerBox()

            if (uiState.downloadSuccess) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "✓ PDF Report generated and ready for viewing.",
                        color = Color(0xFF15803D),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = viewModel::downloadPdf,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = !uiState.isDownloading,
            ) {
                if (uiState.isDownloading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Download Property Pre-Check PDF Report", fontWeight = FontWeight.Bold)
                }
            }

            OutlinedButton(
                onClick = onBackHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Return to Dashboard", color = PrimaryBlue)
            }
        }
    }
}

@Composable
private fun ChecklistItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("•", color = AccentGreen, fontWeight = FontWeight.Bold)
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
    }
}
