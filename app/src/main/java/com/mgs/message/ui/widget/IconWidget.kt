package com.mgs.message.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.mgs.message.R
import com.mgs.message.ui.theme.barIconWidgetSize
import com.mgs.message.ui.theme.horizontalPadding
import com.mgs.message.ui.theme.iconWidgetSize
import com.mgs.message.ui.theme.pictureDescriptionIcon

@Composable
fun IconWidget(modifier: Modifier = Modifier, size: Dp = iconWidgetSize) {
    Image(
        painter = painterResource(R.drawable.message),
        contentDescription = pictureDescriptionIcon,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun IconWidgetWithName(username: String, iconVisibility: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors().copy(Color.Transparent),
        onClick = {}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconVisibility)
                IconWidget(Modifier.padding(end = horizontalPadding / 2), barIconWidgetSize)
            Text(text = username)
        }
    }
}