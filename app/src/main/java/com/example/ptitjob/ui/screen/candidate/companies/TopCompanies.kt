@file:Suppress("DEPRECATION")

package com.example.ptitjob.ui.screen.candidate.companies

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ptitjob.ui.component.CompanyDetailCard
import com.example.ptitjob.ui.component.CompanyDetailData
import com.example.ptitjob.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopCompaniesScreen() {
    val mockTopCompanies = getSampleTopCompanies()
    var currentTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PTITGradientStart, PTITGradientMiddle, PTITGradientEnd),
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                TopCompaniesHeader(
                    currentTab = currentTab,
                    onTabChange = { currentTab = it },
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it }
                )
            }
            
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            PTITBackgroundLight,
                            PTITCornerRadius.xl
                        )
                        .padding(PTITSpacing.lg)
                ) {
                    // Stats Section
                    TopCompaniesStats(companies = mockTopCompanies)
                    
                    PTITSpacing.xl.let { Spacer(Modifier.height(it)) }
                    
                    // Featured Companies Grid
                    Text(
                        text = "TOP CÔNG TY NỔI BẬT NHẤT",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    PTITSpacing.sm.let { Spacer(Modifier.height(it)) }
                    
                    Text(
                        text = "Được xếp hạng dựa trên đánh giá, tăng trưởng và uy tín thương hiệu",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    PTITSpacing.xl.let { Spacer(Modifier.height(it)) }
                    
                    // Company Grid
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 320.dp),
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                        modifier = Modifier.height(1200.dp)
                    ) {
                        items(mockTopCompanies) { company ->
                            TopCompanyCard(
                                company = company,
                                onViewCompany = { /* TODO */ }
                            )
                        }
                    }
                    
                    PTITSpacing.xl.let { Spacer(Modifier.height(it)) }
                    
                    // Pagination
                    PaginationSection()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopCompaniesHeader(
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    val tabs = listOf("Danh sách công ty", "Top công ty")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PTITSpacing.lg)
    ) {
        // Tab Navigation
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = PTITCornerRadius.lg,
            color = Color.White.copy(alpha = 0.9f),
            tonalElevation = PTITElevation.sm
        ) {
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.Transparent,
                contentColor = PTITPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                        color = PTITPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier.padding(PTITSpacing.sm)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab == index,
                        onClick = { onTabChange(index) },
                        text = { 
                            Text(
                                title,
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = PTITPrimary,
                        unselectedContentColor = PTITTextSecondary
                    )
                }
            }
        }
        
        PTITSpacing.xl.let { Spacer(Modifier.height(it)) }
        
        // Header Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = PTITSecondary,
                    modifier = Modifier.size(PTITSize.iconXl)
                )
                Text(
                    text = "Top công ty nổi bật nhất",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PTITTextLight,
                        fontSize = 28.sp
                    )
                )
            }
            
            Text(
                text = "Khám phá những công ty hàng đầu với môi trường làm việc tuyệt vời",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = PTITTextLight.copy(alpha = 0.9f)
                ),
                textAlign = TextAlign.Center
            )
            
            PTITSpacing.md.let { Spacer(Modifier.height(it)) }
            
            // Feature Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                FeatureChip(
                    text = "Đánh giá cao nhất",
                    icon = Icons.Filled.Star,
                    color = PTITSuccess
                )
                FeatureChip(
                    text = "Tăng trưởng nhanh",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = PTITInfo
                )
                FeatureChip(
                    text = "Phúc lợi tốt nhất",
                    icon = Icons.Filled.Verified,
                    color = PTITWarning
                )
            }
            
            PTITSpacing.lg.let { Spacer(Modifier.height(it)) }
            
            // Search Section
            SearchSection(
                searchQuery = searchQuery,
                onSearchChange = onSearchChange
            )
        }
    }
}

@Composable
private fun FeatureChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        shape = PTITCornerRadius.full,
        color = color.copy(alpha = 0.1f),
        tonalElevation = PTITElevation.xs
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
            modifier = Modifier.padding(horizontal = PTITSpacing.md, vertical = PTITSpacing.sm)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        shadowElevation = PTITElevation.md
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { 
                    Text(
                        "Tìm kiếm top công ty",
                        color = PTITTextSecondary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = PTITPrimary
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = PTITNeutral300,
                    focusedBorderColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md,
                singleLine = true
            )
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md,
                contentPadding = PaddingValues(horizontal = PTITSpacing.lg, vertical = PTITSpacing.md)
            ) {
                Text(
                    "Tìm kiếm",
                    fontWeight = FontWeight.SemiBold,
                    color = PTITTextLight
                )
            }
        }
    }
}

@Composable
private fun TopCompaniesStats(companies: List<CompanyDetailData>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.lg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = "${companies.size}",
                label = "Công ty hàng đầu",
                icon = Icons.Default.Business,
                color = PTITPrimary
            )
//            StatItem(
//                value = "${companies.sumOf { it.jobCount }}",
//                label = "Việc làm",
//                icon = Icons.Default.Work,
//                color = PTITSuccess
//            )
            StatItem(
                value = "4.8★",
                label = "Đánh giá TB",
                icon = Icons.Default.Star,
                color = PTITWarning
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(PTITSize.avatarMd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(PTITSize.iconLg)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TopCompanyCard(
    company: CompanyDetailData,
    onViewCompany: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewCompany),
        shape = PTITCornerRadius.lg,
        color = Color.White,
        tonalElevation = PTITElevation.sm
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            // Ranking Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = PTITCornerRadius.sm,
                    color = PTITPrimary,
                    modifier = Modifier.clip(PTITCornerRadius.sm)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                        modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = PTITTextLight,
                            modifier = Modifier.size(PTITSize.iconSm)
                        )
                        Text(
                            text = "TOP ${company.id}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PTITTextLight
                            )
                        )
                    }
                }
                
                IconButton(onClick = { /* TODO: Add to favorites */ }) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        tint = PTITTextSecondary
                    )
                }
            }
            
            PTITSpacing.md.let { Spacer(Modifier.height(it)) }
            
            // Company Info
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company Logo
                Surface(
                    modifier = Modifier.size(PTITSize.avatarLg),
                    shape = PTITCornerRadius.md,
                    color = PTITNeutral50,
                    tonalElevation = PTITElevation.xs
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (company.logo.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(company.logo)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "${company.name} logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = "Company placeholder",
                                modifier = Modifier.size(PTITSize.iconLg),
                                tint = PTITTextSecondary
                            )
                        }
                    }
                }
                
                // Company Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(PTITSpacing.xs)
                ) {
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = company.industry,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary
                        )
                    )
//                    Text(
//                        text = "${company.size} • ${company.address}",
//                        style = MaterialTheme.typography.bodySmall.copy(
//                            color = PTITTextSecondary
//                        ),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
                }
            }
            
            PTITSpacing.lg.let { Spacer(Modifier.height(it)) }
            
            // Company Description
            Text(
                text = company.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            PTITSpacing.lg.let { Spacer(Modifier.height(it)) }
            
            // Company Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                CompanyStatChip(
//                    icon = Icons.Default.Work,
//                    text = "${company.jobCount} việc làm",
//                    color = PTITSuccess
//                )
                CompanyStatChip(
                    icon = Icons.Default.Star,
                    text = "4.${(1..9).random()}★",
                    color = PTITWarning
                )
//                CompanyStatChip(
//                    icon = Icons.Default.Verified,
//                    text = if (company.isVerified) "Xác thực" else "Chưa xác thực",
//                    color = if (company.isVerified) PTITInfo else PTITNeutral400
//                )
            }
            
            PTITSpacing.lg.let { Spacer(Modifier.height(it)) }
            
            // Action Button
            Button(
                onClick = onViewCompany,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = PTITCornerRadius.md
            ) {
                Icon(
                    Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(PTITSize.iconSm)
                )
                PTITSpacing.sm.let { Spacer(Modifier.width(it)) }
                Text(
                    "Xem chi tiết công ty",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CompanyStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Surface(
        shape = PTITCornerRadius.sm,
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
            modifier = Modifier.padding(horizontal = PTITSpacing.sm, vertical = PTITSpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(PTITSize.iconSm)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun PaginationSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = PTITCornerRadius.lg,
            color = Color.White,
            tonalElevation = PTITElevation.sm
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(PTITSpacing.lg)
            ) {
                IconButton(onClick = { /* TODO: Previous page */ }) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous page",
                        tint = PTITTextSecondary
                    )
                }
                
                repeat(5) { index ->
                    val pageNumber = index + 1
                    val isSelected = pageNumber == 1 // Current page
                    
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) PTITPrimary else Color.Transparent,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { /* TODO: Go to page */ }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = pageNumber.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) PTITTextLight else PTITTextSecondary
                                )
                            )
                        }
                    }
                }
                
                IconButton(onClick = { /* TODO: Next page */ }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next page",
                        tint = PTITTextSecondary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun TopCompaniesScreenPreview() {
    MaterialTheme {
        TopCompaniesScreen()
    }
}

private fun getSampleTopCompanies(): List<CompanyDetailData> {
    return listOf(
        CompanyDetailData(
            1, "FPT SOFTWARE", "https://fastly.picsum.photos/id/1/200/200.jpg", "cover",
            "FPT Software là công ty công nghệ thông tin hàng đầu Việt Nam với hơn 25 năm kinh nghiệm phát triển phần mềm. Chúng tôi cung cấp dịch vụ toàn diện từ tư vấn, thiết kế đến triển khai các giải pháp công nghệ tiên tiến.",
            "Công nghệ thông tin", "10000+ nhân viên", "Hà Nội", 156, true, true
        ),
        CompanyDetailData(
            2, "VINGROUP", "https://fastly.picsum.photos/id/2/200/200.jpg", "cover",
            "Vingroup là tập đoàn kinh tế tư nhân đa ngành hàng đầu Việt Nam, hoạt động trong các lĩnh vực bất động sản, bán lẻ, công nghiệp, nông nghiệp và dịch vụ. Với tầm nhìn trở thành tập đoàn hàng đầu khu vực.",
            "Đa ngành", "15000+ nhân viên", "Hà Nội", 234, true, true
        ),
        CompanyDetailData(
            3, "SAMSUNG VIETNAM", "https://fastly.picsum.photos/id/3/200/200.jpg", "cover",
            "Samsung Electronics Việt Nam là chi nhánh của tập đoàn Samsung toàn cầu, chuyên sản xuất các sản phẩm điện tử tiêu dùng và linh kiện bán dẫn. Là một trong những nhà đầu tư FDI lớn nhất tại Việt Nam.",
            "Sản xuất", "20000+ nhân viên", "Bắc Ninh", 95, true, true
        ),
        CompanyDetailData(
            4, "SHOPEE VIETNAM", "https://fastly.picsum.photos/id/4/200/200.jpg", "cover",
            "Shopee là nền tảng thương mại điện tử hàng đầu Đông Nam Á và Đài Loan, cung cấp trải nghiệm mua sắm trực tuyến dễ dàng, an toàn và nhanh chóng thông qua các tính năng mạnh mẽ.",
            "Thương mại điện tử", "3000+ nhân viên", "TP. Hồ Chí Minh", 127, true, true
        ),
        CompanyDetailData(
            5, "VIETCOMBANK", "https://fastly.picsum.photos/id/5/200/200.jpg", "cover",
            "Ngân hàng TMCP Ngoại thương Việt Nam (Vietcombank) là một trong những ngân hàng thương mại hàng đầu Việt Nam với mạng lưới chi nhánh rộng khắp cả nước và quốc tế.",
            "Ngân hàng", "12000+ nhân viên", "Hà Nội", 89, true, true
        ),
        CompanyDetailData(
            6, "GRAB VIETNAM", "https://fastly.picsum.photos/id/6/200/200.jpg", "cover",
            "Grab là siêu ứng dụng hàng đầu Đông Nam Á, cung cấp dịch vụ đi lại, giao hàng, thanh toán và dịch vụ tài chính. Chúng tôi cam kết nâng cao chất lượng cuộc sống cho mọi người.",
            "Siêu ứng dụng", "2000+ nhân viên", "TP. Hồ Chí Minh", 145, true, true
        )
    )
}
