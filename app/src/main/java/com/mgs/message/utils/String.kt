package com.mgs.message.utils

import android.widget.Toast
import com.mgs.message.App

fun String.sendToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.context, this, duration).show()
}