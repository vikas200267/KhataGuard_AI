package com.swagcoders.khataguard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.swagcoders.khataguard.data.model.GraphEdge
import com.swagcoders.khataguard.data.model.GraphNode
import com.swagcoders.khataguard.ui.components.AccentGreen
import com.swagcoders.khataguard.ui.components.DemoModeBanner
import com.swagcoders.khataguard.ui.components.HighSeverityRed
import com.swagcoders.khataguard.ui.components.MedSeverityOrange
import com.swagcoders.khataguard.ui.components.PrimaryBlue
import com.swagcoders.khataguard.viewmodel.GraphViewModel

@Composable
fun GraphScreen(
    caseId: String,
    onViewDiscrepancies: (String) -> Unit,
    viewModel: GraphViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(caseId) {
        viewModel.loadGraph(caseId)
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
                    text = "Property Identity Graph",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                )
                Text(
                    text = "A common property record connecting documents, owners, survey numbers, measurements, and discrepancies.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B),
                )
            }

            if (uiState.isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else {
                val graph = uiState.graph
                if (graph != null) {
                    item {
                        // Graph Summary Metrics Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                MetricItem("Nodes", graph.nodes.size.toString())
                                MetricItem("Edges", graph.edges.size.toString())
                                MetricItem("Entities", graph.nodes.count { it.type != "finding" }.toString())
                                MetricItem("Findings", graph.nodes.count { it.type == "finding" }.toString())
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Identity Graph Nodes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    items(graph.nodes) { node ->
                        GraphNodeCard(node)
                    }

                    item {
                        Text(
                            text = "Relationship Edges (${graph.edges.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    items(graph.edges) { edge ->
                        GraphEdgeRow(edge)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = { onViewDiscrepancies(caseId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            ) {
                Text("Inspect Discrepancy Findings & Next Steps →", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
    }
}

@Composable
private fun GraphNodeCard(node: GraphNode) {
    val (nodeColor, badgeText) = when (node.type) {
        "property_case" -> Pair(PrimaryBlue, "ROOT CASE")
        "document" -> Pair(Color(0xFF0284C7), "DOCUMENT")
        "owner" -> Pair(AccentGreen, "OWNER")
        "property_identifier" -> Pair(Color(0xFF7C3AED), "SURVEY NO")
        "area" -> Pair(Color(0xFFD97706), "AREA MEASUREMENT")
        "finding" -> Pair(HighSeverityRed, "DISCREPANCY")
        else -> Pair(Color(0xFF64748B), node.type.uppercase())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(nodeColor)
                )
                Column {
                    Text(
                        text = node.label ?: node.name ?: node.value ?: node.id,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                    )
                    Text(
                        text = "ID: ${node.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                    )
                }
            }

            Surface(
                color = nodeColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = badgeText,
                    color = nodeColor,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun GraphEdgeRow(edge: GraphEdge) {
    Surface(
        color = Color(0xFFF1F5F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(edge.from, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
            Text("—[ ${edge.relation} ]→", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            Text(edge.to, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
        }
    }
}
