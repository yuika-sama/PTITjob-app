package com.example.ptitjob.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.theme.*

/**
 * Enhanced card component with icon and title for sections
 */
@Composable
fun SectionCard(
    icon: ImageVector, 
    title: String, 
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.sm),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(Modifier.padding(PTITSpacing.xl)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconXl),
                    shape = PTITCornerRadius.sm,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(PTITSpacing.sm)
                            .size(PTITSize.iconMd),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(Modifier.width(PTITSpacing.md))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(PTITSpacing.lg))
            content()
        }
    }
}

/**
 * Enhanced info chip with icon and text
 */
@Composable
fun InfoChip(icon: ImageVector, text: String) {
    SuggestionChip(
        onClick = { /* no-op */ },
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            ) 
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconSm)
            )
        },
        shape = PTITCornerRadius.full,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            labelColor = Color.White,
            iconContentColor = Color.White
        ),
        border = null
    )
}

/**
 * Enhanced loading state component
 */
@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) { 
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(PTITSize.iconXxxl),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
            Text(
                text = "Đang tải...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Enhanced error state with message and retry button
 */
@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)? = null, onBack: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(PTITSpacing.xl),
            shape = PTITCornerRadius.lg,
            elevation = CardDefaults.cardElevation(defaultElevation = PTITElevation.md),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(PTITSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
            ) {
                Surface(
                    modifier = Modifier.size(PTITSize.iconXxxl),
                    shape = PTITCornerRadius.full,
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(PTITSpacing.md)
                            .size(PTITSize.iconXl),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                
                Text(
                    text = "Có lỗi xảy ra",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onRetry?.let {
                        Button(
                            onClick = it,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(PTITSpacing.sm))
                            Text("Thử lại")
                        }
                    }
                    onBack?.let {
                        OutlinedButton(
                            onClick = it,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(PTITSpacing.sm))
                            Text("Quay lại")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enhanced empty state component
 */
@Composable
fun EmptyState(searchTerm: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
    ) {
        Surface(
            modifier = Modifier.size(PTITSize.avatarXxxl),
            shape = PTITCornerRadius.full,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier
                    .padding(PTITSpacing.lg)
                    .size(PTITSize.iconXxxl),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "Không tìm thấy kết quả",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        if (searchTerm.isNotBlank()) {
            Text(
                text = "Không có công ty nào phù hợp với từ khóa \"$searchTerm\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "Thử điều chỉnh bộ lọc hoặc từ khóa tìm kiếm",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Enhanced row item for key-value display
 */
@Composable
fun RowItem(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Enhanced contact row for displaying contact information
 */
@Composable
fun ContactRow(label: String, value: String?) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.sm,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value?.takeIf { it.isNotBlank() } ?: "Chưa cập nhật",
                style = MaterialTheme.typography.bodyMedium,
                color = if (value.isNullOrBlank())
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}