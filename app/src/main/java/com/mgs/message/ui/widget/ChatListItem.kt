package com.mgs.message.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mgs.message.data.Chat

@Composable
fun ChatListItem(chat: Chat, onClick: () -> Unit = {}) {
    ListItem(
        headlineContent = { Text(chat.target.name) },
        supportingContent = { Text("message") },
        trailingContent = { Text("meta") },
        leadingContent = {
            IconWidget(enabled = false)
        },
        modifier = Modifier.clickable { onClick() }
    )
    HorizontalDivider()
}