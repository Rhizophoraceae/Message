package com.mgs.message.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mgs.message.App
import com.mgs.message.data.Message
import com.mgs.message.ui.theme.horizontalPadding
import com.mgs.message.ui.theme.iconWidgetSize
import com.mgs.message.ui.theme.verticalPadding

@Composable
fun MessageListItem(
    message: Message,
    iconVisibility: Boolean = true,
    usernameVisibility: Boolean = false
) {
    val isCurrentUser = (message.userId == App.currentUser.id)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        if (iconVisibility && !isCurrentUser)
            IconWidgetOnMessage()
        else
            Spacer(Modifier.width(iconWidgetSize))

        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            modifier = Modifier
                .padding(
                    start = horizontalPadding / 2,
                    end = horizontalPadding / 2
                )
                .weight(1f)
        ) {
            if (usernameVisibility)
                Text(
                    text = message.userId.toString(),
                    modifier = Modifier.padding(bottom = horizontalPadding / 2)
                )
            Card(
                shape = RoundedCornerShape(iconWidgetSize / 2),
                colors = if (isCurrentUser) CardDefaults.cardColors()
                    .copy(MaterialTheme.colorScheme.secondaryContainer) else CardDefaults.cardColors(),
                modifier = Modifier.defaultMinSize(
                    minHeight = iconWidgetSize,
                    minWidth = iconWidgetSize
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.defaultMinSize(
                        minHeight = iconWidgetSize,
                        minWidth = iconWidgetSize
                    )
                ) {
                    Text(
                        text = message.content,
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = verticalPadding / 2
                        )
                    )
                }
            }
        }

        if (iconVisibility && isCurrentUser)
            IconWidgetOnMessage()
        else
            Spacer(Modifier.width(iconWidgetSize))
    }
}