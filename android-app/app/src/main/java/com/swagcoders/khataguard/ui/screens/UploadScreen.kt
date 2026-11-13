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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.swagcoders.khataguard.ui.components.DocumentCard
import com.swagcoders.khataguard.ui.components.PrimaryBlue
import com.swagcoders.khataguard.viewmodel.UploadViewModel

@Composable
fun UploadScreen(
    caseId: String,
    onProceedToAnalysis: (String) -> Unit,
    viewModel: UploadViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(caseId) {
        viewModel.initCase(caseId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF)),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        DemoModeBanner()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "Upload Property Documents",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                )
                Text(
                    text = "Upload at least 2 documents (e.g. Sale Deed & Khata Extract) to cross-verify ownership, survey numbers, and land area.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                )
            }

            // Quick Synthetic Demo Document Inserters
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Add Demonstration Documents",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.addDemoDocument("Sale_Deed_Sy42_3.pdf", "Sale Deed") },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("+ Sale Deed (1200 sq ft)", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { viewModel.addDemoDocument("Khata_Extract_42_3.pdf", "Khata Record") },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("+ e-Khata (1080 sq ft)", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Uploaded Documents (${uiState.documents.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (uiState.isUploading) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp), strokeWidth = 2.dp)
                    }
                }
            }

            if (uiState.documents.isEmpty()) {
                item {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "No documents uploaded yet. Tap one of the demo document buttons above to test the AI cross-check pipeline.",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            } else {
                items(uiState.documents) { doc ->
                    DocumentCard(
                        title = doc.filename,
                        subtitle = "Type: ${doc.documentType} • Size: ${doc.sizeBytes / 1024} KB",
                        badgeText = "Ready for AI",
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            val canProceed = uiState.documents.size >= 2
            Button(
                onClick = { onProceedToAnalysis(caseId) },
                enabled = canProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                Text(
                    text = if (canProceed) "Run AI Analysis & Cross-Check →" else "Upload At Least 2 Documents to Continue",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}
