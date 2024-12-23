package com.mgs.message.lib.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mgs.message.R
import com.mgs.message.ui.theme.AppTheme

open class BaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LoginPage()
                }
            }
        }
    }
}

@Composable
fun WidgetColumn() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            modifier = Modifier.align(alignment = Alignment.Top),
            text = "Hello",
            color = Color.Blue,
            fontSize = 20.sp
        )
        Button(onClick = {}) {
            Text(
                text = "Hello",
                color = Color.White,
                fontSize = 20.sp
            )
        }
        TextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(text = "Type something here")
            }
        )
        Image(
            painter = painterResource(id = R.drawable.logo_theme),
            contentDescription = "logo"
        )

//        AsyncImage(
//            model = "https://avatars.githubusercontent.com/u/80232659?s=48&v=4",
//            contentDescription = "icon"
//        )

        CircularProgressIndicator()

        LinearProgressIndicator()
    }
}

@Preview
@Composable
fun LoginPage() {
    Card(
        modifier = Modifier.wrapContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.activity_vertical_margin)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_theme),
                contentDescription = "logo"
            )
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = stringResource(R.string.textview_username)
                    )
                },
            )
            TextField(
                value = "",
                onValueChange = {},
                placeholder = {
                    Text(
                        text = stringResource(R.string.textview_password)
                    )
                }
            )
            Row {
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = stringResource(R.string.button_login)
                    )
                }
                Button(
                    onClick = {}
                ) {
                    Text(
                        text = stringResource(R.string.button_register)
                    )
                }
            }
        }
    }

}