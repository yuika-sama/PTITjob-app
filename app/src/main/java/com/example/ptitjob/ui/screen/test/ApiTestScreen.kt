package com.example.ptitjob.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreen(
    viewModel: ApiTestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val testResults by viewModel.testResults.collectAsState()
    
    var selectedCategory by remember { mutableStateOf("Auth") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Test Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.clearResults() }) {
                        Icon(Icons.Default.Clear, "Clear Results")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Category Tabs
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status Card
            StatusCard(uiState)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // API Test Buttons based on category
            when (selectedCategory) {
                "Auth" -> AuthTestSection(viewModel)
                "User" -> UserTestSection(viewModel)
                "Job" -> JobTestSection(viewModel)
                "Company" -> CompanyTestSection(viewModel)
                "Category" -> CategoryTestSection(viewModel)
                "Location" -> LocationTestSection(viewModel)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Results List
            Text(
                text = "Test Results (${testResults.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(testResults.reversed()) { result ->
                    TestResultCard(result)
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("Auth", "User", "Job", "Company", "Category", "Location")
    
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        categories.forEach { category ->
            Tab(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(category) }
            )
        }
    }
}

@Composable
fun StatusCard(state: ApiTestState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (state) {
                is ApiTestState.Loading -> MaterialTheme.colorScheme.secondary
                is ApiTestState.Success -> Color(0xFF4CAF50)
                is ApiTestState.Error -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (state) {
                is ApiTestState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Loading...", color = Color.White)
                }
                is ApiTestState.Success -> {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(state.message, color = Color.White)
                }
                is ApiTestState.Error -> {
                    Icon(Icons.Default.Clear, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(state.message, color = Color.White)
                }
                else -> {
                    Icon(Icons.Default.Info, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Ready to test APIs")
                }
            }
        }
    }
}

@Composable
fun AuthTestSection(viewModel: ApiTestViewModel) {
    var email by remember { mutableStateOf("test@example.com") }
    var password by remember { mutableStateOf("password123") }
    var fullName by remember { mutableStateOf("Test User") }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Authentication APIs", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.testLogin(email, password) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Login")
            }
            
            Button(
                onClick = { viewModel.testRegister(email, password, fullName) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Register")
            }
        }
        
        Button(
            onClick = { viewModel.testGetCurrentUser() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get Current User")
        }
    }
}

@Composable
fun UserTestSection(viewModel: ApiTestViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("User APIs", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { viewModel.testGetAllUsers() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Person, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get All Users")
        }
    }
}

@Composable
fun JobTestSection(viewModel: ApiTestViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Job APIs", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { viewModel.testGetAllJobs() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get All Jobs")
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Query") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { viewModel.testSearchJobs(searchQuery) },
            modifier = Modifier.fillMaxWidth(),
            enabled = searchQuery.isNotBlank()
        ) {
            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Search Jobs")
        }
    }
}

@Composable
fun CompanyTestSection(viewModel: ApiTestViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Company APIs", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { viewModel.testGetAllCompanies() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get All Companies")
        }
    }
}

@Composable
fun CategoryTestSection(viewModel: ApiTestViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Category APIs", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { viewModel.testGetAllCategories() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get All Categories")
        }
    }
}

@Composable
fun LocationTestSection(viewModel: ApiTestViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Location APIs", style = MaterialTheme.typography.titleMedium)
        
        Button(
            onClick = { viewModel.testGetAllLocations() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Get All Locations")
        }
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Query") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = { viewModel.testSearchLocations(searchQuery) },
            modifier = Modifier.fillMaxWidth(),
            enabled = searchQuery.isNotBlank()
        ) {
            Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Search Locations")
        }
    }
}

@Composable
fun TestResultCard(result: TestResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.success) 
                Color(0xFFE8F5E9) 
            else 
                Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (result.success) Icons.Default.CheckCircle else Icons.Default.Clear,
                contentDescription = null,
                tint = if (result.success) Color(0xFF4CAF50) else Color(0xFFE57373),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.apiName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = result.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTimestamp(result.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
