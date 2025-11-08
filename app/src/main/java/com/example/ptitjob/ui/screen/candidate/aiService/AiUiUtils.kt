package com.example.ptitjob.ui.screen.candidate.aiService

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ptitjob.ui.theme.PTITCornerRadius
import com.example.ptitjob.ui.theme.PTITSpacing
import com.example.ptitjob.ui.theme.PTITTextPrimary
import com.example.ptitjob.ui.theme.PTITWarning
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun copyUriToTempFile(
    context: Context,
    uri: Uri,
    prefix: String = "upload_",
    suffix: String? = null
): Pair<File, String> = withContext(Dispatchers.IO) {
    println("🔍 AiUiUtils - Starting file copy")
    println("📁 URI: $uri")
    
    val resolver = context.contentResolver
    runCatching { resolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }

    val displayName = resolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    } ?: "file_${System.currentTimeMillis()}"
    
    println("📄 Display name: $displayName")

    val extension = suffix ?: displayName.substringAfterLast('.', "").let { ext ->
        if (ext.isNotBlank()) ".${ext.lowercase(Locale.ROOT)}" else ""
    }
    
    println("📝 Extension: $extension")

    val tempFile = File.createTempFile(prefix, if (extension.isNotEmpty()) extension else ".tmp", context.cacheDir)
    println("📂 Temp file created: ${tempFile.absolutePath}")

    resolver.openInputStream(uri)?.use { input ->
        FileOutputStream(tempFile).use { output ->
            val bytesWritten = input.copyTo(output)
            println("💾 Bytes written: $bytesWritten")
        }
    } ?: error("Không thể mở file đã chọn")
    
    println("✅ File copy completed")
    println("📊 Final file size: ${tempFile.length()} bytes")
    println("🔍 File exists: ${tempFile.exists()}")
    println("📖 File readable: ${tempFile.canRead()}")

    tempFile to displayName
}

@Composable
fun AiErrorBanner(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    retryLabel: String = "Thử lại"
) {
    Surface(
        modifier = modifier,
        color = PTITWarning.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, PTITWarning.copy(alpha = 0.5f)),
        shape = PTITCornerRadius.md
    ) {
        Row(
            modifier = Modifier.padding(PTITSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(PTITSpacing.sm)
        ) {
            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = PTITWarning)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(color = PTITTextPrimary),
                modifier = Modifier.weight(1f)
            )
            if (onRetry != null) {
                TextButton(onClick = onRetry) {
                    Text(retryLabel, fontWeight = FontWeight.SemiBold, color = PTITWarning)
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = Color.Black.copy(alpha = 0.6f))
            }
        }
    }
}
