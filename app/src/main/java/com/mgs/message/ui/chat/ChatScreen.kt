package com.mgs.message.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mgs.message.data.Chat
import com.mgs.message.data.Group
import com.mgs.message.data.User
import com.mgs.message.ui.theme.titleChat
import com.mgs.message.ui.widget.ChatListItem
import com.mgs.message.ui.widget.IconWidget

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ChatScreen() {
    val navigator = rememberListDetailPaneScaffoldNavigator<Chat>()

    val chatList: MutableList<Chat> = mutableListOf(
        Chat(0, Group("Mystery Group")),
        Chat(1, User("Rhizophoraceae"))
    )
    for (i in 2..10) {
        chatList.add(Chat(i))
    }

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                Column {
                    TopAppBar(
                        title = { Text(text = titleChat) },
                        actions = {
                            IconWidget()
                        }
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(items = chatList, key = { chat ->
                            chat.id
                        }) { item ->
                            ChatListItem(chat = item, onClick = {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, item)
                            })
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.content?.target?.let {
                    MessageScreen(it)
                }
            }
        }
    )
}