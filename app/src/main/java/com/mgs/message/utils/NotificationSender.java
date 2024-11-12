package com.mgs.message.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.mgs.message.R;

public class NotificationSender {
    public static void Send(Context context, NotificationManager notificationManager, String title, String content, String icon) {
        String channelId = "用户消息";
        @SuppressLint("ResourceType") Notification notification = new Notification.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.icon_notification)
                .setWhen(System.currentTimeMillis())
                .build();
        NotificationChannel channel = new NotificationChannel(channelId, "用户消息", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(1029, notification);
    }
}
