package com.example.ptitjob.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITGradientEnd
import com.example.ptitjob.ui.theme.PTITGradientMiddle
import com.example.ptitjob.ui.theme.PTITGradientStart
import com.example.ptitjob.ui.theme.PTITNeutral50
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PtitjobTheme

/**
 * Demo screen to test new PTIT Bottom Navigation
 * Shows how to structure pages with proper layout and spacing
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavbarDemoScreen(
    title: String = "Demo Screen",
    onBack: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.SemiBold) },
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
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd)
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(PTITSpacing.lg),
                verticalArrangement = Arrangement.spacedBy(PTITSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PTITNeutral50.copy(alpha = 0.9f)
                    ),
                    shape = PTITCornerRadius.lg,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PTITSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                    ) {
                        Text(
                            text = "🎉 PTIT Bottom Navigation",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = "Navbar mới với theme PTIT, hiệu ứng đẹp mắt và navigation thông minh",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = PTITTextSecondary
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Features
                DemoFeatureCard(
                    title = "✨ Hiệu ứng Animation",
                    description = "Smooth transitions, scaling effects, và color animations khi chuyển tab"
                )

                DemoFeatureCard(
                    title = "🎨 PTIT Theme",
                    description = "Gradient backgrounds, PTIT colors, và typography nhất quán"
                )

                DemoFeatureCard(
                    title = "📱 Responsive Design", 
                    description = "Tự động điều chỉnh layout và padding cho các màn hình khác nhau"
                )

                DemoFeatureCard(
                    title = "🧭 Smart Navigation",
                    description = "Tự động highlight tab dựa trên route hiện tại, state preservation"
                )

                DemoFeatureCard(
                    title = "🔧 Easy Integration",
                    description = "Chỉ cần wrap trong PTITAppContainer hoặc sử dụng PTITScaffold"
                )

                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = PTITPrimary.copy(alpha = 0.1f)
                    ),
                    shape = PTITCornerRadius.lg,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(PTITSpacing.lg),
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
                    ) {
                        Text(
                            text = "📋 Hướng dẫn sử dụng",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITPrimary
                            )
                        )
                        
                        Text(
                            text = "• Navbar sẽ tự động hiện ở tất cả trang (trừ auth)\n" +
                                  "• Tab active được highlight với animation\n" +
                                  "• Layout tự động điều chỉnh safe area\n" +
                                  "• Hỗ trợ nested navigation routes",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PTITTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoFeatureCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PTITNeutral50.copy(alpha = 0.8f)
        ),
        shape = PTITCornerRadius.md,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PTITTextPrimary
                )
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavbarDemoScreenPreview() {
    PtitjobTheme {
        NavbarDemoScreen(
            title = "Demo Screen",
            onBack = {}
        )
    }
}