package com.example.ptitjob.ui.screen.candidate.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.theme.*
import kotlinx.coroutines.delay


// --- Component Màn hình Chính ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotFound404Screen(
    onBack: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Lỗi 404",
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PTITPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PTITGradientStart,
                            PTITGradientMiddle,
                            PTITGradientEnd
                        )
                    )
                )
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PTITSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    NotFound404Illustration()
                }
                
                Spacer(Modifier.height(PTITSpacing.xxl))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 }
                    ) + fadeIn()
                ) {
                    NotFound404Content()
                }
                
                Spacer(Modifier.height(PTITSpacing.xxl))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 3 }
                    ) + fadeIn()
                ) {
                    NotFound404Actions()
                }
                
                Spacer(Modifier.height(PTITSpacing.xxl))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 4 }
                    ) + fadeIn()
                ) {
                    NotFound404HelpSection()
                }
                
                Spacer(Modifier.height(PTITSpacing.xl))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn()
                ) {
                    NotFound404Footer()
                }
            }
        }
    }
}
}


// --- Các Composable con ---

@Composable
private fun NotFound404Illustration() {
    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background gradient circle
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PTITError.copy(alpha = 0.2f),
                            PTITError.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Main 404 text
        Surface(
            modifier = Modifier.size(150.dp),
            shape = CircleShape,
            color = Color.White,
            tonalElevation = PTITElevation.lg
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "404",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PTITError
                    )
                )
            }
        }
        
        // Error icon decoration
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = (-10).dp)
                .size(40.dp),
            shape = CircleShape,
            color = PTITError
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Lỗi",
                    tint = Color.White,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
            }
        }
    }
}

@Composable
private fun NotFound404Content() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.md
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Text(
                text = "Oops! Trang không tồn tại",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Trang bạn đang tìm kiếm có thể đã được di chuyển, xóa hoặc không bao giờ tồn tại.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Đừng lo lắng! Hãy thử một trong các lựa chọn dưới đây để tiếp tục.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotFound404Actions() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            NotFound404ActionButton(
                text = "Về trang chủ",
                icon = Icons.Default.Home,
                isPrimary = true,
                onClick = { /* TODO: Go Home */ }
            )
            
            NotFound404ActionButton(
                text = "Quay lại",
                icon = Icons.Default.ArrowBack,
                isPrimary = false,
                onClick = { /* TODO: Go Back */ }
            )
            
            NotFound404ActionButton(
                text = "Tìm kiếm công việc",
                icon = Icons.Default.Search,
                isPrimary = false,
                onClick = { /* TODO: Search Jobs */ }
            )
        }
    }
}

@Composable
private fun NotFound404ActionButton(
    text: String,
    icon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PTITPrimary
            ),
            shape = PTITCornerRadius.md,
            contentPadding = PaddingValues(PTITSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconMd)
            )
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PTITPrimary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(
                    listOf(PTITPrimary, PTITSecondary)
                )
            ),
            shape = PTITCornerRadius.md,
            contentPadding = PaddingValues(PTITSpacing.md)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(PTITSize.iconMd)
            )
            Spacer(Modifier.width(PTITSpacing.sm))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun NotFound404HelpSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = PTITInfo.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = PTITInfo,
                    modifier = Modifier.size(PTITSize.iconMd)
                )
                Text(
                    text = "Vẫn gặp khó khăn?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextPrimary
                    )
                )
            }
            
            Text(
                text = "Nếu bạn tin rằng đây là một lỗi hoặc cần hỗ trợ, vui lòng liên hệ với chúng tôi:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                textAlign = TextAlign.Center
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
            ) {
                TextButton(
                    onClick = { /* TODO: Email */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PTITInfo
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Spacer(Modifier.width(PTITSpacing.xs))
                    Text("Email hỗ trợ")
                }
                
                TextButton(
                    onClick = { /* TODO: Phone */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = PTITInfo
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(PTITSize.iconSm)
                    )
                    Spacer(Modifier.width(PTITSpacing.xs))
                    Text("Hotline")
                }
            }
        }
    }
}

@Composable
private fun NotFound404Footer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.md,
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Text(
            text = "PTIT Job - Nền tảng tuyển dụng hàng đầu cho sinh viên PTIT",
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(PTITSpacing.md)
        )
    }
}


// --- Preview ---
@Preview(showBackground = true, widthDp = 800, heightDp = 800)
@Composable
fun NotFound404ScreenPreview() {
    MaterialTheme {
        Surface {
            NotFound404Screen(onBack = {})
        }
    }
}