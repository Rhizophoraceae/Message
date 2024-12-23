package com.mgs.message.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.mgs.message.ui.theme.AppTheme
import com.mgs.message.ui.theme.descriptionSend
import com.mgs.message.ui.theme.horizontalPadding
import com.mgs.message.ui.theme.pictureDescriptionBack
import com.mgs.message.ui.theme.pictureDescriptionFriendInfo
import com.mgs.message.ui.theme.pictureDescriptionSend
import com.mgs.message.ui.theme.sendWidgetVerticalPadding
import com.mgs.message.ui.theme.verticalPadding
import com.mgs.message.ui.widget.IconWidgetWithName
import com.mgs.message.ui.widget.MessageWidget

@Preview
@Composable
fun MessagePage() {
    AppTheme {
//        val list = ('A'..'Z').map { it.toString() }
        val list = listOf(
            "AAAAAAAAAAAAAAAAAAAAAAAA",
            "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
            "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB",
            "测",
            "CCCCCCCCCCCCCCCCCCCCC",
            "测试测试测试测试",
            "测试测试测试测试测试测试",
            "测试测试测试测试测试",
            "测试测试测试测试测试测试测试测试测试测试测试测试",
            "D",
            "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试",
            "Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word Word "
        )
        Scaffold(
            topBar = {
                MessageAppBarWidget()
            },
            bottomBar = {
                MessageSendWidget()
            }
        ) { innerPadding ->
            MessageListWidget(list = list, paddingValues = innerPadding)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageListWidget(
    list: List<String>,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val listState = rememberLazyListState()
        val imeVisible = WindowInsets.Companion.isImeVisible
        if (imeVisible) {
            if (listState.isScrollInProgress) {
                val offset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
                if (offset != 0) {
                    val focusManager = LocalFocusManager.current
                    focusManager.clearFocus()
                }
            }
        }
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(verticalPadding),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding()
                        + verticalPadding / 2,
                bottom = paddingValues.calculateBottomPadding()
                        + verticalPadding / 2
            ),
            reverseLayout = true,
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .focusRequester(focusRequester)
        ) {
            items(list) { message ->
                MessageWidget(message)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageAppBarWidget() {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors()
            .copy(MaterialTheme.colorScheme.surfaceContainer),
        title = {
            IconWidgetWithName("Username")
        },
        navigationIcon = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = pictureDescriptionBack
                )
            }
        },
        actions = {
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = pictureDescriptionFriendInfo
                )
            }
        }
    )
}

@Composable
fun MessageSendWidget() {
    var state by remember { mutableStateOf("") }
    val localStyle = LocalTextStyle.current
    val mergedStyle = localStyle.merge(TextStyle(color = LocalContentColor.current))
    Box(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
    ) {
        Card(
            shape = RectangleShape,
            colors = CardDefaults.cardColors().copy(Color.Transparent),
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
        ) {
            BasicTextField(
                value = state,
                onValueChange = { newValue ->
                    state = newValue
                },
                singleLine = true,
                textStyle = mergedStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Card(
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(
                                horizontal = horizontalPadding,
                                vertical = verticalPadding / 2
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        horizontal = horizontalPadding,
                                        vertical = sendWidgetVerticalPadding
                                    )
                            ) {
                                innerTextField()
                                if (state.isEmpty())
                                    Text(
                                        text = descriptionSend,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                            }

                            IconButton(
                                onClick = {},
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = pictureDescriptionSend
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}