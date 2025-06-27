package com.mgs.message.state

import androidx.compose.foundation.text.input.TextFieldState
import com.mgs.androidlib.state.BaseViewState

data class LoginUiState(
    val username: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false
): BaseViewState()