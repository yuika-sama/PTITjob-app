package com.example.ptitjob.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

// Giả lập các kiểu dữ liệu từ code React
data class JobCategory(val id: String, val name: String)
data class Location(val id: Int, val name: String)

// --- Component chính ---
@Composable
fun SearchSection(
    locations: List<Location> = emptyList(),
    categories: List<JobCategory> = emptyList(),
    isLoading: Boolean = false
) {
    // State management tương đương useState trong React
    var selectedCategory by remember { mutableStateOf<JobCategory?>(null) }
    var searchKeyword by remember { mutableStateOf("") }

    // Giả lập hook useSearchJobs
    var searchLoading by remember { mutableStateOf(false) }
    var searchError by remember { mutableStateOf<String?>(null) }

    // Tương đương hàm handleSearch
    val handleSearch = { keyword: String, locationId: Int? ->
        // Logic tìm kiếm sẽ được đặt ở đây, thường là gọi một ViewModel
        println("Searching for: keyword=$keyword, locationId=$locationId, categoryId=${selectedCategory?.id}")
    }

    // Tương đương hàm handleSuggestionClick
    val handleSuggestionClick = { keyword: String ->
        searchKeyword = keyword
        // Logic điều hướng đến trang tìm kiếm
        println("Navigate to search with suggestion: $keyword")
    }

    // Tương đương hàm handleClearCategory
    val handleClearCategory = {
        selectedCategory = null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        EnhancedHeroHeader()

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchBar(
            locations = locations,
            onSearch = { keyword, locationId -> handleSearch(keyword, locationId) },
            isLoading = searchLoading,
            initialKeyword = searchKeyword
        )

        // Hiển thị lỗi tìm kiếm
        searchError?.let {
            Text(
                text = "Lỗi tìm kiếm: $it",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Hiển thị bộ lọc category đang được chọn
        selectedCategory?.let { category ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Bộ lọc:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = true,
                    onClick = { /* Do nothing */ },
                    label = { Text("\uD83C\uDFE2 ${category.name}") }, // Emoji 🏢
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Xóa bộ lọc",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { handleClearCategory() }
                        )
                    }
                )
            }
        }

        // Gợi ý tìm kiếm
        SearchSuggestions(onSuggestionClick = handleSuggestionClick)

        Spacer(modifier = Modifier.height(16.dp))

        // Bố cục nội dung chính
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Danh sách Category (chỉ là placeholder)
            CategoryList(categories = categories, modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung bên phải
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeroBanner(onPlayVideo = { println("Play video") })
                StatsBar(onRefresh = { println("Refresh stats") })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


// --- Các Composable con ---

@Composable
fun EnhancedHeroHeader() {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_animation")

    // Animation cho background (tương đương @keyframes backgroundShift)
    val backgroundOffsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "bg_shift"
    )

    // Animation cho hiệu ứng xoay (tương đương @keyframes rotate)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotate"
    )

    // Animation cho hiệu ứng text phát sáng (tương đương @keyframes textGlow)
    val textGlowRadius by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ), label = "text_glow"
    )
    val textGlowColor = Color.White.copy(alpha = 0.5f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFFD32F2F),
                        Color(0xFF1976D2),
                        Color(0xFF0D47A1),
                        Color(0xFF1A237E)
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset.Infinite
                )
            )
            .padding(vertical = 64.dp, horizontal = 16.dp)
            .graphicsLayer { translationX = backgroundOffsetX } // Áp dụng animation background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // PTIT Brand Logo
            Box(contentAlignment = Alignment.Center) {
                // Vòng xoay animation bên ngoài
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .background(
                            brush = Brush.sweepGradient(
                                0.0f to Color.Transparent,
                                0.5f to Color.White.copy(alpha = 0.3f),
                                1.0f to Color.Transparent
                            ),
                            shape = CircleShape
                        )
                )
                // Logo
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(3.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter("https://bizweb.dktcdn.net/thumb/grande/100/390/135/files/logo-white-circle.png?v=1749438223850"),
                        contentDescription = "PTIT Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Heading
            val headingShadow = Shadow(
                color = textGlowColor,
                blurRadius = with(LocalDensity.current) { textGlowRadius.dp.toPx() }
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(brush = Brush.linearGradient(listOf(Color.White, Color(0xFFE3F2FD))))){
                        append("Khám phá cơ hội nghề nghiệp\n")
                    }
                    withStyle(style = SpanStyle(
                        brush = Brush.linearGradient(listOf(Color(0xFFFFEB3B), Color(0xFFFFC107))),
                        fontSize = 38.sp // Tương đương 0.9em
                    )) {
                        append("cùng PTIT Job")
                    }
                },
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                style = LocalTextStyle.current.copy(shadow = headingShadow)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = buildAnnotatedString {
                    append("Nền tảng việc làm hàng đầu dành cho sinh viên và cựu sinh viên\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = Color(0xFFFFEB3B))) {
                        append("Học viện Công nghệ Bưu chính Viễn thông")
                    }
                },
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature Cards
            FeatureCards()
        }
    }
}

@Composable
fun FeatureCards() {
    val features = listOf(
        Triple("🚀", "Cơ hội IT hàng đầu", "Việc làm công nghệ từ startup đến tập đoàn"),
        Triple("🤝", "Mạng lưới doanh nghiệp", "Kết nối với 2000+ công ty uy tín"),
        Triple("📈", "Phát triển sự nghiệp", "Định hướng và nâng cao kỹ năng")
    )

    // Sử dụng LazyRow để có thể cuộn ngang nếu không đủ không gian
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(features.size) { index ->
            val (icon, title, desc) = features[index]
            FeatureCard(icon, title, desc)
        }
    }
}

@Composable
fun FeatureCard(icon: String, title: String, desc: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .width(250.dp) // Đặt chiều rộng cố định cho các card
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(text = icon, fontSize = 40.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}


// --- Placeholder Composables ---
// Đây là các component giả lập để mã nguồn có thể biên dịch được.
// Bạn sẽ cần tự xây dựng các component này dựa trên thiết kế của mình.

@Composable
fun SearchBar(locations: List<Location>, onSearch: (String, Int?) -> Unit, isLoading: Boolean, initialKeyword: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Đây là Search Bar", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun SearchSuggestions(onSuggestionClick: (String) -> Unit) {
    Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onSuggestionClick("ReactJS") }) { Text("ReactJS") }
        Button(onClick = { onSuggestionClick("Kotlin") }) { Text("Kotlin") }
        Button(onClick = { onSuggestionClick("DevOps") }) { Text("DevOps") }
    }
}

@Composable
fun CategoryList(categories: List<JobCategory>, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text("Đây là Danh sách Category", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun HeroBanner(onPlayVideo: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text("Đây là Hero Banner")
        }
    }
}

@Composable
fun StatsBar(onRefresh: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text("Đây là Stats Bar", modifier = Modifier.padding(16.dp))
    }
}

// --- Preview ---

@Preview(showBackground = true, widthDp = 1200)
@Composable
fun SearchSectionPreview() {
    val sampleCategories = listOf(
        JobCategory("1", "Công nghệ thông tin"),
        JobCategory("2", "Marketing"),
        JobCategory("3", "Thiết kế")
    )
    MaterialTheme {
        SearchSection(categories = sampleCategories)
    }
}