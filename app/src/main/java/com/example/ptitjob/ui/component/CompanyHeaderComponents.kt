package com.example.ptitjob.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.ptitjob.data.model.Company

/**
 * Breadcrumbs component for navigation
 */
@Composable
fun DetailBreadcrumbs(companyName: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Danh sách công ty")
        }
        Text(">", modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(companyName, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Company header with gradient background
 */
@Composable
fun CompanyHeader(company: Company, onBack: () -> Unit) {
    Box(
        modifier = Modifier.background(
            Brush.linearGradient(listOf(Color(0xFFDE221A), Color(0xFFB01B14)))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Breadcrumbs
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White.copy(0.8f))
                    Text("Danh sách công ty", color = Color.White.copy(0.8f))
                }
                Text(">", color = Color.White.copy(0.8f))
                Text(
                    company.name,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                // Logo (fallback nếu trống)
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, Color.White.copy(0.2f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (company.logo.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = company.logo),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(0.85f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        company.name,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        company.size?.let { InfoChip(icon = Icons.Default.Person, text = it) }
                        company.address?.let { InfoChip(icon = Icons.Default.LocationOn, text = it) }
                        company.jobCount?.takeIf { it > 0 }?.let {
                            InfoChip(icon = Icons.Default.Person, text = "$it việc làm")
                        }
                        // Thêm chip ngành nếu muốn
                        InfoChip(icon = Icons.Default.Info, text = company.industry)
                    }
                }
            }
        }
    }
}