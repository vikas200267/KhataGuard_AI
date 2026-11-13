package com.swagcoders.khataguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swagcoders.khataguard.data.model.Finding

val PrimaryBlue = Color(0xFF1565C0)
val DarkBlue = Color(0xFF0D47A1)
val AccentGreen = Color(0xFF2E7D32)
val LightBg = Color(0xFFF8FBFF)
val HighSeverityRed = Color(0xFFD32F2F)
val MedSeverityOrange = Color(0xFFE65100)
val LowSeverityGreen = Color(0xFF2E7D32)

@Composable
fun DemoModeBanner(modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFE3F2FD),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DEMO MODE — Operating with synthetic property intelligence",
                style = MaterialTheme.typography.labelSmall,
                color = DarkBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun SeverityBadge(severity: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (severity.lowercase()) {
        "high" -> Triple(Color(0xFFFFEBEE), HighSeverityRed, "HIGH SEVERITY")
        "medium" -> Triple(Color(0xFFFFF3E0), MedSeverityOrange, "MEDIUM SEVERITY")
        else -> Triple(Color(0xFFE8F5E9), LowSeverityGreen, "LOW SEVERITY")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier,
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun FindingCard(
    finding: Finding,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = finding.findingType.replace("_", " ").titlecase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                )
                SeverityBadge(severity = finding.severity)
            }

            Text(
                text = finding.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569),
            )

            if (finding.sourceReferences.isNotEmpty()) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Source Evidence: ${finding.sourceReferences.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    )
                }
            }

            if (finding.recommendedNextStep.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text("Recommendation:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = AccentGreen)
                    Text(finding.recommendedNextStep, style = MaterialTheme.typography.bodySmall, color = Color(0xFF334155))
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    title: String,
    subtitle: String,
    badgeText: String? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            if (badgeText != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = badgeText,
                        color = AccentGreen,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun DisclaimerBox(modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFFFF8E1),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Legal Disclaimer",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100),
            )
            Text(
                text = "KhataGuard AI provides preliminary document intelligence and cross-document discrepancy screening. It does not certify legal ownership or replace certified encumbrance search and legal due diligence.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D4037),
            )
        }
    }
}

@Composable
fun LoadingStateView(message: String = "Analyzing property documents...") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator(color = PrimaryBlue)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
    }
}

fun String.titlecase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}
