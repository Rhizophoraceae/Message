package com.rjgc.zhs.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastSender {
    public static void send(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
