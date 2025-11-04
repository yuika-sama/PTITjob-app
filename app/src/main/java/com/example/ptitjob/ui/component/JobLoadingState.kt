package com.example.ptitjob.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// --- Component Chính ---
@Composable
fun JobLoadingState(
    isLoading: Boolean,
    error: String?,
    onGoBack: () -> Unit,
    content: @Composable () -> Unit
) {
    // Sử dụng when để xử lý các trạng thái khác nhau, rất giống với if/else if/else
    when {
        isLoading -> {
            LoadingStateUI()
        }
        error != null -> {
            ErrorStateUI(error = error, onGoBack = onGoBack)
        }
        else -> {
            // Hiển thị nội dung con khi không có lỗi và không đang tải
            content()
        }
    }
}

// --- Giao diện cho trạng thái Đang tải ---
@Composable
private fun LoadingStateUI() {
    val shimmerBrush = createShimmerBrush(true)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp), // Tương đương py: 4
        verticalArrangement = Arrangement.spacedBy(24.dp) // Tương đương mb: 3
    ) {
        // Skeleton cho header
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(shimmerBrush, shape = RoundedCornerShape(8.dp))
        )
        // Skeleton cho nội dung chính
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp) // Tương đương gap: 3
        ) {
            // Cột trái
            Spacer(
                modifier = Modifier
                    .weight(2f)
                    .height(400.dp)
                    .background(shimmerBrush, shape = RoundedCornerShape(8.dp))
            )
            // Cột phải
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .height(300.dp)
                    .background(shimmerBrush, shape = RoundedCornerShape(8.dp))
            )
        }
    }
}

// --- Giao diện cho trạng thái Lỗi ---
@Composable
private fun ErrorStateUI(error: String, onGoBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon và thông báo lỗi
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Lỗi"
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = error)
                }
                // Nút hành động
                TextButton(onClick = onGoBack) {
                    Text("Quay lại")
                }
            }
        }
    }
}

// --- Hàm trợ giúp để tạo hiệu ứng Shimmer ---
@Composable
fun createShimmerBrush(showShimmer: Boolean = true): Brush {
    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        // animationSpec specifies the animation curve and duration
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerTranslate"
    )
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

// --- Previews để xem trước các trạng thái ---
@Preview(showBackground = true, name = "Loading State")
@Composable
fun JobLoadingStatePreview_Loading() {
    MaterialTheme {
        JobLoadingState(
            isLoading = true,
            error = null,
            onGoBack = {},
            content = { /* Nội dung sẽ không hiển thị */ }
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun JobLoadingStatePreview_Error() {
    MaterialTheme {
        JobLoadingState(
            isLoading = false,
            error = "Không thể tải chi tiết công việc. Vui lòng thử lại.",
            onGoBack = {},
            content = { /* Nội dung sẽ không hiển thị */ }
        )
    }
}

@Preview(showBackground = true, name = "Success State")
@Composable
fun JobLoadingStatePreview_Success() {
    MaterialTheme {
        JobLoadingState(
            isLoading = false,
            error = null,
            onGoBack = {}
        ) {
            // Đây là nơi "children" sẽ được hiển thị
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Nội dung trang đã tải thành công!",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}