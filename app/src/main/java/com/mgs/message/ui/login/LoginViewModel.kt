package com.mgs.message.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mgs.message.state.LoginUiState

class LoginViewModel : ViewModel() {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun login() {
        Log.i("111", "Username: " + uiState.username.text.toString())
        Log.i("111", "Password: " + uiState.password.text.toString())
    }
}