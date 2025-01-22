package com.mgs.message

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.mgs.message.data.User
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var currentUser: User
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        currentUser = User(
            "Rhizophoraceae",
            1
        )
    }
}