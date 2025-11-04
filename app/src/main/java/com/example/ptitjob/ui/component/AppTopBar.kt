package com.example.ptitjob.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ptitjob.ui.theme.PTITPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    centerAligned: Boolean = false,
    showNotification: Boolean = false,
    onNotificationClick: (() -> Unit)? = null,
    containerColor: Color = PTITPrimary,
    titleColor: Color = Color.White,
    navigationIconColor: Color = Color.White,
    actionIconColor: Color = Color.White
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        titleContentColor = titleColor,
        navigationIconContentColor = navigationIconColor,
        actionIconContentColor = actionIconColor
    )

    val navigationIcon: (@Composable () -> Unit)? = if (showBack && onBack != null) {
        {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        }
    } else null

    val actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
        if (showNotification && onNotificationClick != null) {
            IconButton(onClick = onNotificationClick) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "Thông báo")
            }
        }
    }

    if (centerAligned) {
        CenterAlignedTopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            colors = colors,
            modifier = modifier
        )
    } else {
        TopAppBar(
            title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            colors = colors,
            modifier = modifier
        )
    }
}


