package com.mgs.message.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mgs.message.ui.chat.ChatScreen
import com.mgs.message.ui.login.LoginScreen
import com.mgs.message.ui.theme.AppTheme
import com.mgs.message.utils.navigateSingleTopTo

@Preview
@Composable
fun MessageApp() {
    AppTheme {
        val navController = rememberNavController()

        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        val items = listOf(Chat, Group, Account)
        var currentScreen = items.find { it.route == (currentDestination?.route) } ?: Chat

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                items.forEach { item ->
                    item(
                        icon = {
                            Icon(
                                imageVector =
                                if (currentScreen == item)
                                    item.icon
                                else
                                    item.iconOutlined,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentScreen == item,
                        onClick = {
                            currentScreen = item
                            navController.navigateSingleTopTo(currentScreen.route)
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Chat.route,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                composable(route = Chat.route) {
                    ChatScreen()
                }
                composable(route = Group.route) {
                    LoginScreen()
                }
                composable(route = Account.route) {
                    LoginScreen()
                }
            }
        }
    }
}