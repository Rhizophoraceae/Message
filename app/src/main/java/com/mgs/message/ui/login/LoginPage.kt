package com.mgs.message.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mgs.message.R
import com.mgs.message.ui.theme.AppTheme
import com.mgs.message.ui.theme.buttonLogin
import com.mgs.message.ui.theme.buttonRegister
import com.mgs.message.ui.theme.descriptionHidePassword
import com.mgs.message.ui.theme.descriptionShowPassword
import com.mgs.message.ui.theme.logoWidgetPadding
import com.mgs.message.ui.theme.logoWidgetSize
import com.mgs.message.ui.theme.pictureDescriptionLogo
import com.mgs.message.ui.theme.textFieldPassword
import com.mgs.message.ui.theme.textFieldUsername
import com.mgs.message.ui.theme.verticalPadding

@Preview
@Composable
fun LoginPage(viewModel: LoginViewModel = viewModel()) {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .padding(verticalPadding)
                        .width(IntrinsicSize.Min),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_logo_theme),
                        contentDescription = pictureDescriptionLogo,
                        modifier = Modifier
                            .padding(vertical = logoWidgetPadding)
                            .size(logoWidgetSize)
                    )

                    UsernameInputWidget(viewModel.uiState.username)

                    PasswordInputWidget(viewModel.uiState.password)

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.login()
                        }
                    ) {
                        Text(text = buttonLogin)
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                        }
                    ) {
                        Text(text = buttonRegister)
                    }
                }
            }
        }
    }

}

@Composable
fun UsernameInputWidget(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        state = state,
        label = {
            Text(textFieldUsername)
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier.padding(bottom = verticalPadding)
    )
}

@Composable
fun PasswordInputWidget(
    state: TextFieldState,
    modifier: Modifier = Modifier
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    OutlinedSecureTextField(
        state = state,
        label = {
            Text(textFieldPassword)
        },
        textObfuscationMode =
        if (passwordHidden) TextObfuscationMode.RevealLastTyped
        else TextObfuscationMode.Visible,
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                val description =
                    if (passwordHidden) descriptionShowPassword
                    else descriptionHidePassword
                Icon(
                    imageVector = visibilityIcon,
                    contentDescription = description
                )
            }
        },
        modifier = modifier.padding(bottom = verticalPadding)
    )
}