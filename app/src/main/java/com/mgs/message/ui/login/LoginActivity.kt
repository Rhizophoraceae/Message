package com.mgs.message.ui.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.mgs.androidlib.activity.BaseActivity

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LoginScreen() }
        viewModel = ViewModelProvider(this)[LoginViewModel::class]
    }
}