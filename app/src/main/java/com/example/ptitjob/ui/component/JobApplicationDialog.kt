package com.example.ptitjob.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// --- Data Model cho Form ---
data class ApplicationFormData(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val cvMethod: String = "url",
    val cvUrl: String = "",
    val coverLetter: String = ""
)

// --- Data Model cho Job (phiên bản đơn giản) ---
data class JobInfo(
    val id: String,
    val title: String,
    val companyName: String
)

// --- Component Chính ---
@Composable
fun JobApplicationDialog(
    open: Boolean,
    onClose: () -> Unit,
    job: JobInfo?,
) {
    if (open && job != null) {
        // --- State Management ---
        var formData by remember { mutableStateOf(ApplicationFormData()) }
        var isSubmitting by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        var success by remember { mutableStateOf(false) }

        Dialog(
            onDismissRequest = { if (!isSubmitting) onClose() }
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp) // Tương đương borderRadius: 3
            ) {
                Column {
                    // --- Header ---
                    DialogHeader(job = job, isSubmitting = isSubmitting, onClose = onClose)
                    HorizontalDivider()

                    // --- Content (Form) ---
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .weight(1f, fill = false) // Để nội dung co giãn
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (success) {
                            SuccessAlert(message = "Đơn ứng tuyển đã được gửi thành công!")
                        }
                        error?.let {
                            ErrorAlert(message = it)
                        }

                        // Form Sections
                        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                            PersonalInfoSection(
                                formData = formData,
                                onFormDataChange = { formData = it },
                                isEnabled = !isSubmitting
                            )
                            CvSection(
                                formData = formData,
                                onFormDataChange = { formData = it },
                                isEnabled = !isSubmitting
                            )
                            CoverLetterSection(
                                formData = formData,
                                onFormDataChange = { formData = it },
                                isEnabled = !isSubmitting
                            )
                        }
                    }

                    HorizontalDivider()

                    // --- Actions ---
                    DialogActions(
                        isSubmitting = isSubmitting,
                        onSubmit = { /* TODO: Logic submit */ },
                        onCancel = onClose
                    )
                }
            }
        }
    }
}


// --- Các Composable con ---

@Composable
private fun DialogHeader(job: JobInfo, isSubmitting: Boolean, onClose: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp, 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Ứng tuyển",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onClose, enabled = !isSubmitting) {
                Icon(Icons.Default.Close, contentDescription = "Đóng")
            }
        }
        Text(
            text = "${job.title} - ${job.companyName}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PersonalInfoSection(formData: ApplicationFormData, onFormDataChange: (ApplicationFormData) -> Unit, isEnabled: Boolean) {
    FormSection(title = "Thông tin cá nhân") {
        OutlinedTextField(
            value = formData.fullName,
            onValueChange = { onFormDataChange(formData.copy(fullName = it)) },
            label = { Text("Họ và tên *") },
            placeholder = { Text("Họ tên hiển thị với NTD") },
            modifier = Modifier.fillMaxWidth(),
            enabled = isEnabled
        )
        Row(horizontalArrangement  = Arrangement.spacedBy(16.dp)){
            OutlinedTextField(
                value = formData.email,
                onValueChange = { onFormDataChange(formData.copy(email = it)) },
                label = { Text("Email *") },
                placeholder = { Text("Email liên lạc") },
                modifier = Modifier.weight(1f),
                enabled = isEnabled
            )
            OutlinedTextField(
                value = formData.phone,
                onValueChange = { onFormDataChange(formData.copy(phone = it)) },
                label = { Text("SDT *") },
                placeholder = { Text("SĐT liên lạc") },
                modifier = Modifier.weight(1f),
                enabled = isEnabled
            )
        }
    }
}

@Composable
private fun CvSection(formData: ApplicationFormData, onFormDataChange: (ApplicationFormData) -> Unit, isEnabled: Boolean) {
    val cvOptions = listOf("url" to "URL CV")
    FormSection(title = "Phương thức nộp CV") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            cvOptions.forEach { (value, label) ->
                Row(
                    Modifier.selectable(
                        selected = (formData.cvMethod == value),
                        onClick = { onFormDataChange(formData.copy(cvMethod = value, cvUrl = "")) },
                        role = Role.RadioButton,
                        enabled = isEnabled && value != "upload"
                    ).padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (formData.cvMethod == value),
                        onClick = null, // onClick is handled by the Row
                        enabled = isEnabled && value != "upload"
                    )
                    Text(text = label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
        if (formData.cvMethod == "url") {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = formData.cvUrl,
                    onValueChange = { onFormDataChange(formData.copy(cvUrl = it)) },
                    label = { Text("URL CV *") },
                    placeholder = { Text("https://drive.google.com/...") },
                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEnabled,
                    singleLine = true
                )

                Text(
                    text = "Vui lòng nhập đường dẫn đến CV của bạn (Google Drive, etc.)",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun CoverLetterSection(formData: ApplicationFormData, onFormDataChange: (ApplicationFormData) -> Unit, isEnabled: Boolean) {
    FormSection(title = "Thư giới thiệu", description = "Một thư giới thiệu ngắn gọn, chỉn chu sẽ giúp bạn gây ấn tượng hơn với nhà tuyển dụng.") {
        OutlinedTextField(
            value = formData.coverLetter,
            onValueChange = { onFormDataChange(formData.copy(coverLetter = it)) },
            placeholder = { Text("Viết giới thiệu ngắn gọn về bản thân, nêu rõ mong muốn, lý do bạn muốn ứng tuyển...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            enabled = isEnabled
        )
    }
}

@Composable
private fun DialogActions(isSubmitting: Boolean, onSubmit: () -> Unit, onCancel: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onCancel, enabled = !isSubmitting) {
            Text("Hủy")
        }
        Button(
            onClick = onSubmit,
            enabled = !isSubmitting,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đang nộp đơn...")
            } else {
                Text("Nộp hồ sơ")
            }
        }
    }
}

@Composable
private fun FormSection(title: String, description: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        description?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        content()
    }
}

@Composable
fun SuccessAlert(message: String) { /* Implementation using Card */ }
@Composable
fun ErrorAlert(message: String) { /* Implementation using Card */ }

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun JobApplicationDialogPreview() {
    val sampleJob = JobInfo(
        id = "123",
        title = "Senior Android Developer (Kotlin, Compose)",
        companyName = "Global Tech Solutions"
    )
    MaterialTheme {
        // To preview a dialog, we need a host Composable
        Box(modifier = Modifier.fillMaxSize()) {
            JobApplicationDialog(open = true, onClose = {}, job = sampleJob)
        }
    }
}