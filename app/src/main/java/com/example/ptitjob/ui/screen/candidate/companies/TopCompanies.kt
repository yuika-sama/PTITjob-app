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
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .background(PTITPrimary)
        )
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
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(PTITBackgroundLight)
                        .padding(PTITSpacing.lg)
                ) {
                    // Stats Section
                    TopCompaniesStats(companies = mockTopCompanies)

                    Spacer(Modifier.height(PTITSpacing.xl))

                    // Featured Companies Title
                    Text(
                        text = "TOP CÔNG TY NỔI BẬT NHẤT",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary,
                            fontSize = 24.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(PTITSpacing.sm))

                    Text(
                        text = "Được xếp hạng dựa trên đánh giá, tăng trưởng và uy tín thương hiệu",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PTITTextSecondary,
                            fontSize = 14.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(PTITSpacing.xl))

                    // Company List (Changed from Grid to Column)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(PTITSpacing.md)
                    ) {
                        mockTopCompanies.forEach { company ->
                            TopCompanyCard(
                                company = company,
                                onViewCompany = { /* TODO */ }
                            )
                        }
                    }

                    Spacer(Modifier.height(PTITSpacing.xl))

                    // Pagination
                    PaginationSection()
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
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f),
            shadowElevation = 2.dp
        ) {
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.Transparent,
                contentColor = PTITPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                        color = PTITPrimary,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTab == index,
                        onClick = { onTabChange(index) },
                        modifier = Modifier.padding(vertical = PTITSpacing.sm),
                        text = {
                            Text(
                                title,
                                fontWeight = if (currentTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = PTITPrimary,
                        unselectedContentColor = PTITTextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(PTITSpacing.xl))

        // Header Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = PTITSecondary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(PTITSpacing.md))

        // Title
        Text(
            text = "Top công ty nổi bật nhất",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 28.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(PTITSpacing.sm))

        Text(
            text = "Khám phá những công ty hàng đầu với môi trường làm việc tuyệt vời",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 15.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(PTITSpacing.lg))

        // Feature Chips
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
            ) {
                FeatureChip(
                    text = "Đánh giá cao nhất",
                    icon = Icons.Filled.Star,
                    color = PTITSecondary
                )
                FeatureChip(
                    text = "Tăng trưởng nhanh",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = PTITSecondary
                )
            }
        }

        Spacer(Modifier.height(PTITSpacing.xl))

        // Search Section
        SearchSection(
            searchQuery = searchQuery,
            onSearchChange = onSearchChange
        )
    }
}

@Composable
private fun FeatureChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
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
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        "Tìm kiếm top công ty",
                        color = PTITTextSecondary.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = PTITPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = PTITNeutral300.copy(alpha = 0.5f),
                    focusedBorderColor = PTITPrimary,
                    unfocusedContainerColor = PTITNeutral50.copy(alpha = 0.3f),
                    focusedContainerColor = PTITNeutral50.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PTITPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Text(
                    "Tìm kiếm",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun TopCompaniesStats(companies: List<CompanyDetailData>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
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
            StatItem(
                value = "4.8★",
                label = "Đánh giá TB",
                icon = Icons.Default.Star,
                color = PTITSecondary
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
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = PTITTextPrimary,
                fontSize = 24.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = PTITTextSecondary,
                fontSize = 13.sp
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
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(PTITSpacing.lg)
        ) {
            // Top Row: Ranking Badge + Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PTITPrimary
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TOP ${company.id}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = PTITPrimary.copy(alpha = 0.08f)
                ) {
                    IconButton(onClick = { /* TODO: Add to favorites */ }) {
                        Icon(
                            Icons.Default.FavoriteBorder,
                            contentDescription = "Add to favorites",
                            tint = PTITPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(PTITSpacing.md))

            // Company Info Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company Logo
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = PTITNeutral50,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (company.logo.isNotBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(company.logo)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "${company.name} logo",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Icon(
                                Icons.Default.Business,
                                contentDescription = "Company placeholder",
                                modifier = Modifier.size(32.dp),
                                tint = PTITTextSecondary.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                // Company Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = company.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PTITTextPrimary,
                            fontSize = 18.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = company.industry,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PTITTextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(PTITSpacing.md))

            // Company Description
            Text(
                text = company.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PTITTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(PTITSpacing.md))

            HorizontalDivider(
                color = PTITNeutral300.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            Spacer(Modifier.height(PTITSpacing.md))

            // Company Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompanyStatChip(
                    icon = Icons.Default.Star,
                    text = "4.${(1..9).random()}★",
                    color = PTITSecondary
                )

                // Action Button
                Button(
                    onClick = onViewCompany,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PTITPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Xem chi tiết công ty",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
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
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
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
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PTITSpacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(PTITSpacing.md)
            ) {
                IconButton(
                    onClick = { /* TODO: Previous page */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous page",
                        tint = PTITTextSecondary
                    )
                }

                repeat(5) { index ->
                    val pageNumber = index + 1
                    val isSelected = pageNumber == 1

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
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else PTITTextSecondary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { /* TODO: Next page */ },
                    modifier = Modifier.size(40.dp)
                ) {
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