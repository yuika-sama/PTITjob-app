package com.example.ptitjob.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestDetailScreen(
    viewModel: ApiTestViewModel = hiltViewModel()
) {
    var showResponseDialog by remember { mutableStateOf(false) }
    var selectedResponse by remember { mutableStateOf("") }
    
    val uiState by viewModel.uiState.collectAsState()
    val testResults by viewModel.testResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("API Test Dashboard")
                        Text(
                            "Total Tests: ${testResults.size}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { viewModel.clearResults() }) {
                        Icon(Icons.Default.Clear, "Clear Results", tint = Color.White)
                    }
                    IconButton(onClick = { showResponseDialog = true }) {
                        Icon(Icons.Default.Info, "Info", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Quick test all */ },
                icon = { Icon(Icons.Default.PlayArrow, null) },
                text = { Text("Quick Test") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Quick Stats Card
            QuickStatsCard(testResults)
            
            // Main Content
            Box(modifier = Modifier.weight(1f)) {
                ApiTestScreen()
            }
        }
    }
    
    if (showResponseDialog) {
        InfoDialog(onDismiss = { showResponseDialog = false })
    }
}

@Composable
fun QuickStatsCard(results: List<TestResult>) {
    val successCount = results.count { it.success }
    val failCount = results.count { !it.success }
    val successRate = if (results.isNotEmpty()) {
        (successCount.toFloat() / results.size * 100).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.CheckCircle,
                label = "Success",
                value = successCount.toString(),
                color = Color(0xFF4CAF50)
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                thickness = DividerDefaults.Thickness, color = DividerDefaults.color
            )

            StatItem(
                icon = Icons.Default.Close,
                label = "Failed",
                value = failCount.toString(),
                color = Color(0xFFE57373)
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                thickness = DividerDefaults.Thickness, color = DividerDefaults.color
            )

            StatItem(
                icon = Icons.Default.Info,
                label = "Success Rate",
                value = "$successRate%",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        "API Test Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoSection(
                    title = "🔐 Authentication",
                    items = listOf(
                        "Login: Test user authentication",
                        "Register: Create new user account",
                        "Get Current User: Retrieve logged-in user info"
                    )
                )
                
                InfoSection(
                    title = "👥 User Management",
                    items = listOf(
                        "Get All Users: Fetch all registered users",
                        "Search Users: Find users by criteria"
                    )
                )
                
                InfoSection(
                    title = "💼 Job Management",
                    items = listOf(
                        "Get All Jobs: Fetch all job listings",
                        "Search Jobs: Find jobs by title/location"
                    )
                )
                
                InfoSection(
                    title = "🏢 Company & Others",
                    items = listOf(
                        "Companies: Manage company data",
                        "Categories: Job categories",
                        "Locations: Available locations"
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "⚠️ Note:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "Make sure your API base URL is configured correctly in NetworkModule.kt",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Got it!")
                }
            }
        }
    }
}

@Composable
fun InfoSection(title: String, items: List<String>) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    items.forEach { item ->
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text("• ", color = MaterialTheme.colorScheme.primary)
            Text(
                item,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ResponseDialog(
    response: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "API Response",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
                
                // Response content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1E1E))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        item {
                            Text(
                                response,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF4EC9B0),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
