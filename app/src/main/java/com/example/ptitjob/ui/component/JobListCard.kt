package com.example.ptitjob.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.ptitjob.ui.theme.PTITBackgroundLight
import com.example.ptitjob.ui.theme.PTITGray100
import com.example.ptitjob.ui.theme.PTITGray200
import com.example.ptitjob.ui.theme.PTITGray300
import com.example.ptitjob.ui.theme.PTITGray50
import com.example.ptitjob.ui.theme.PTITGray600
import com.example.ptitjob.ui.theme.PTITOnPrimary
import com.example.ptitjob.ui.theme.PTITPrimary
import com.example.ptitjob.ui.theme.PTITSecondary
import com.example.ptitjob.ui.theme.PTITShadowDark
import com.example.ptitjob.ui.theme.PTITShadowMedium
import com.example.ptitjob.ui.theme.PTITSuccess
import com.example.ptitjob.ui.theme.PTITSurfaceLight
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITTextSecondary
import com.example.ptitjob.ui.theme.PTITWarning
import com.example.ptitjob.ui.theme.PtitjobTheme

// --- Data Model ---
data class JobListCardData(
    val id: Int,
    val backendId: String = id.toString(),
    val title: String,
    val company: String,
    val companyLogo: String?,
    val logoUrl: String?,
    val salary: String,
    val location: String,
    val experience: String?,
    val postedTime: String,
    val deadline: String?,
    val isUrgent: Boolean,
    val isVerified: Boolean,
    val tags: List<String>?
)

// --- Component Chính ---
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun JobListCard(
    job: JobListCardData,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit,
    onCardClick: (JobListCardData) -> Unit = {}
) {
    var isSaved by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 12.dp else 4.dp, 
        label = "elevation"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isHovered) PTITPrimary else PTITGray300,
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(job) }
            .hoverable(interactionSource)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = PTITShadowMedium,
                spotColor = PTITShadowDark
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = PTITSurfaceLight
        )
    ) {
        if (isLandscape && configuration.screenWidthDp > 600) {
            // Layout ngang cho tablet/desktop
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Top
            ) {
                CompanyLogo(logoUrl = job.logoUrl, companyName = job.company)
                
                Column(modifier = Modifier.weight(1f)) {
                    JobMainInfo(job = job)
                    Spacer(Modifier.height(16.dp))
                    InfoRow(job = job)
                    Spacer(Modifier.height(16.dp))
                    TagsRow(tags = job.tags)
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SalaryInfo(salary = job.salary, isUrgent = job.isUrgent)
                    ActionButtons(
                        job = job,
                        isSaved = isSaved,
                        onApply = { onApply(job) },
                        onSave = {
                            isSaved = !isSaved
                            onSave(job)
                        },
                        isCompact = false
                    )
                }
            }
        } else {
            // Layout dọc cho mobile
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header row với logo và thông tin cơ bản
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompanyLogo(
                        logoUrl = job.logoUrl,
                        companyName = job.company,
                        isCompact = true
                    )
                    
                    Column(modifier = Modifier.weight(1f)) {
                        JobMainInfo(job = job, isCompact = true)
                    }
                    
                    // Save button
                    IconButton(
                        onClick = {
                            isSaved = !isSaved
                            onSave(job)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSaved) PTITPrimary.copy(alpha = 0.1f) else PTITGray100,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Lưu việc làm",
                            tint = if (isSaved) PTITPrimary else PTITGray600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Salary và urgent info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SalaryInfo(salary = job.salary, isUrgent = job.isUrgent, isCompact = true)
                    
                    // Apply button
                    Button(
                        onClick = { onApply(job) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PTITPrimary,
                            contentColor = PTITOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        ),
                        modifier = Modifier
                            .height(44.dp)
                            .widthIn(min = 120.dp)
                    ) {
                        Text(
                            "Ứng tuyển",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Info row
                InfoRow(job = job, isCompact = true)
                
                Spacer(Modifier.height(12.dp))
                
                // Tags
                TagsRow(tags = job.tags, isCompact = true)
            }
        }
    }
}

// --- Các Composable con ---

@Composable
private fun CompanyLogo(
    logoUrl: String?, 
    companyName: String,
    isCompact: Boolean = false
) {
    val size = if (isCompact) 56.dp else 80.dp
    val cornerRadius = if (isCompact) 12.dp else 16.dp
    if (logoUrl != null) {
        val context = LocalContext.current
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(logoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "$companyName logo",
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PTITGray50,
                            PTITGray100
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = PTITGray200,
                    shape = RoundedCornerShape(cornerRadius)
                ),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PTITPrimary.copy(alpha = 0.1f),
                            PTITSecondary.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = PTITPrimary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(cornerRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = companyName.take(2).uppercase(),
                style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PTITPrimary
            )
        }
    }
}

@Composable
private fun JobMainInfo(
    job: JobListCardData,
    isCompact: Boolean = false
) {
    Column {
        Text(
            text = job.title,
            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = if (isCompact) 2 else 3,
            overflow = TextOverflow.Ellipsis,
            color = PTITTextPrimary,
            lineHeight = if (isCompact) 20.sp else 24.sp
        )
        
        Spacer(Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = job.company,
                style = MaterialTheme.typography.bodyMedium,
                color = PTITTextSecondary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, false)
            )
            
            if (job.isVerified) {
                Surface(
                    shape = CircleShape,
                    color = PTITSuccess.copy(alpha = 0.1f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = "Công ty đã xác thực",
                        tint = PTITSuccess,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    job: JobListCardData,
    isCompact: Boolean = false
) {
    val iconSize = if (isCompact) 14.dp else 16.dp
    val fontSize = if (isCompact) 12.sp else 13.sp
    
    if (isCompact) {
        // Layout dọc cho mobile
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip(
                    icon = Icons.Default.LocationOn,
                    text = job.location,
                    iconSize = iconSize,
                    fontSize = fontSize
                )
                job.experience?.let {
                    InfoChip(
                        icon = Icons.Default.Work,
                        text = it,
                        iconSize = iconSize,
                        fontSize = fontSize
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                job.deadline?.let {
                    InfoChip(
                        icon = Icons.Default.DateRange,
                        text = "Còn $it",
                        iconSize = iconSize,
                        fontSize = fontSize
                    )
                }
                InfoChip(
                    icon = Icons.Default.AccessTime,
                    text = job.postedTime,
                    iconSize = iconSize,
                    fontSize = fontSize
                )
            }
        }
    } else {
        // Layout ngang cho tablet/desktop
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoChip(
                icon = Icons.Default.LocationOn,
                text = job.location,
                iconSize = iconSize,
                fontSize = fontSize
            )
            job.experience?.let {
                InfoChip(
                    icon = Icons.Default.Work,
                    text = it,
                    iconSize = iconSize,
                    fontSize = fontSize
                )
            }
            job.deadline?.let {
                InfoChip(
                    icon = Icons.Default.DateRange,
                    text = "Còn $it",
                    iconSize = iconSize,
                    fontSize = fontSize
                )
            }
            InfoChip(
                icon = Icons.Default.AccessTime,
                text = job.postedTime,
                iconSize = iconSize,
                fontSize = fontSize
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsRow(
    tags: List<String>?,
    isCompact: Boolean = false
) {
    if (!tags.isNullOrEmpty()) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val maxTags = if (isCompact) 3 else 5
            val tagsToShow = tags.take(maxTags)
            
            tagsToShow.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PTITPrimary.copy(alpha = 0.08f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = PTITPrimary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.clickable { /* Handle tag click */ }
                ) {
                    Text(
                        text = tag,
                        fontSize = if (isCompact) 10.sp else 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PTITPrimary,
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 8.dp else 10.dp,
                            vertical = if (isCompact) 4.dp else 6.dp
                        )
                    )
                }
            }
            
            // Hiển thị số lượng tag còn lại nếu có
            if (tags.size > maxTags) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PTITGray100,
                    border = BorderStroke(
                        width = 1.dp,
                        color = PTITGray300
                    )
                ) {
                    Text(
                        text = "+${tags.size - maxTags}",
                        fontSize = if (isCompact) 10.sp else 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PTITGray600,
                        modifier = Modifier.padding(
                            horizontal = if (isCompact) 8.dp else 10.dp,
                            vertical = if (isCompact) 4.dp else 6.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SalaryInfo(
    salary: String, 
    isUrgent: Boolean,
    isCompact: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isCompact) Arrangement.Start else Arrangement.End
    ) {
        Text(
            text = salary,
            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PTITSuccess
        )
        
        if (isUrgent) {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = PTITWarning.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, PTITWarning.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = PTITWarning,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "GẤP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PTITWarning
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    job: JobListCardData,
    isSaved: Boolean,
    onApply: (JobListCardData) -> Unit,
    onSave: (JobListCardData) -> Unit,
    isCompact: Boolean = false
) {
    if (isCompact) {
        // Mobile layout - chỉ hiển thị nút Apply, Save button đã được đặt ở header
        return
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { onApply(job) },
            colors = ButtonDefaults.buttonColors(
                containerColor = PTITPrimary,
                contentColor = PTITOnPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            modifier = Modifier
                .width(140.dp)
                .height(44.dp)
        ) {
            Text(
                "Ứng tuyển",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
        
        IconButton(
            onClick = { onSave(job) },
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = if (isSaved) PTITPrimary.copy(alpha = 0.1f) else PTITGray100,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Lưu việc làm",
                tint = if (isSaved) PTITPrimary else PTITGray600,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector? = null, 
    text: String,
    iconSize: androidx.compose.ui.unit.Dp = 16.dp,
    fontSize: TextUnit = 13.sp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = PTITTextSecondary,
                modifier = Modifier.size(iconSize)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = fontSize),
            color = PTITTextSecondary,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}