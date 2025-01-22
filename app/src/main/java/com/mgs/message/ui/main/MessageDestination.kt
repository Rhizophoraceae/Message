package com.mgs.message.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.mgs.message.ui.theme.titleAccount
import com.mgs.message.ui.theme.titleChat
import com.mgs.message.ui.theme.titleGroup
import com.mgs.message.ui.theme.titleMessage

interface MessageDestination {
    val icon: ImageVector
    val iconOutlined: ImageVector
    val route: String
    val label: String
}

object Chat : MessageDestination {
    override val icon = Icons.AutoMirrored.Filled.Chat
    override val iconOutlined = Icons.AutoMirrored.Outlined.Chat
    override val route = "chat"
    override val label = titleChat
}

object Group : MessageDestination {
    override val icon = Icons.Filled.Group
    override val iconOutlined = Icons.Outlined.Group
    override val route = "group"
    override val label = titleGroup
}

object Account : MessageDestination {
    override val icon = Icons.Filled.AccountCircle
    override val iconOutlined = Icons.Outlined.AccountCircle
    override val route = "account"
    override val label = titleAccount
}

object Message : MessageDestination {
    override val icon = Icons.Filled.Forum
    override val iconOutlined = Icons.Outlined.Forum
    override val route = "message"
    override val label = titleMessage
    const val idArg: String = "id"
    val arguments = listOf(
        navArgument(idArg) { type = NavType.StringType }
    )
}