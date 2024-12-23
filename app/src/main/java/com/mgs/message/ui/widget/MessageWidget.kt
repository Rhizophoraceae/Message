package com.mgs.message.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mgs.message.ui.theme.horizontalPadding
import com.mgs.message.ui.theme.iconWidgetSize
import com.mgs.message.ui.theme.verticalPadding

@Preview
@Composable
fun MessageWidget(
    message: String = "message",
    iconVisibility: Boolean = true,
    usernameVisibility: Boolean = false
) {
    Row(
        modifier = Modifier.padding(horizontal = horizontalPadding)
    ) {
        if (iconVisibility)
            IconWidget()
        Column(
            modifier = Modifier.padding(
                start = horizontalPadding / 2,
                end = horizontalPadding / 2 + iconWidgetSize
            )
        ) {
            if (usernameVisibility)
                Text(
                    text = "Username",
                    modifier = Modifier.padding(bottom = horizontalPadding / 2)
                )
            Card(
                shape = RoundedCornerShape(iconWidgetSize / 2),
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
                        text = message,
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = verticalPadding / 2
                        )
                    )
                }
            }
        }
    }
}