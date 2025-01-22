package com.mgs.message.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.mgs.message.data.GroupObject;
import com.mgs.message.data.UserObject;
import com.mgs.message.utils.NotificationSender;
import com.mgs.message.utils.WebSocketClient;
import com.mgs.message.data.MessageObject;
import com.mgs.message.data.MessageEvent;
import com.mgs.message.data.Setting;
import com.mgs.message.utils.CurrentUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageService extends Service {
    private NotificationManager notificationManager;
    private Context context;
    private AppInfo appInfo;
    private static final int TV_SHOW = 1; //消息内容
    private static final int TV_ERROR = 2; //异常信息

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        appInfo = new AppInfo();
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        EventBus.getDefault().register(this); //EventBus注册
        new Thread(() -> {
            getIcon();
            WebSocketClient.connect(CurrentUser.hostIp, CurrentUser.hostPort, CurrentUser.userObject.getUserId()); // 发起连接
        }).start();
        Log.i("service", "服务启动");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(MessageEvent messageEvent) {
        switch (messageEvent.getNumber()) {
            case TV_SHOW:
                String json = messageEvent.getMessage();
                for (int i = 0; i < json.length(); i++) {
                    if (json.charAt(i) == '：') {
                        json = json.substring(i + 1);
                        break;
                    }
                }
                Log.i("json", json);
                Intent intent = new Intent();
                intent.setAction("message");
                intent.putExtra("message", json);
                sendBroadcast(intent);
                MessageObject messageObject = new Gson().fromJson(json, MessageObject.class);
                if (messageObject.getIsGroup() == 0)
                    NotificationSender.Send(context, notificationManager, messageObject.getUsername(), messageObject.getContent(), "icon");
                else
                    NotificationSender.Send(context, notificationManager, messageObject.getGroupName(), messageObject.getUsername() + ": " + messageObject.getContent(), "icon");
                break;
            case TV_ERROR:
                Log.i("service", "异常信息");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //取消注册
    }

    private void getIcon() {
        try {
            URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/" + CurrentUser.userObject.getIcon());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            CurrentUser.icon = BitmapFactory.decodeStream(connection.getInputStream());
            CurrentUser.iconMap.put(CurrentUser.userObject.getIcon(), CurrentUser.icon);
            Log.i("icon", "图片: " + CurrentUser.icon);
        } catch (IOException e) {
            Log.i("icon", "图片获取失败");
            e.printStackTrace();
        }
    }

    class SaveReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appInfo.saveInfo();
        }
    }

    class LoadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appInfo.loadInfo();
        }
    }

    public static class AppInfo {
        private UserObject userObject;
        private Bitmap icon;
        private List<UserObject> userObjectList;
        private List<GroupObject> groupObjectList;
        private List<Setting> settingList;
        private List<UserObject> memberList;
        private HashMap<String, Bitmap> iconMap;
        private HashMap<String, Bitmap> iconMapGroup;
        private HashMap<Integer, HashMap<String, Bitmap>> iconMapGroupMember;
        private String hostIp;
        private String hostPort;
        private int toId;
        private int isGroup;

        public AppInfo() {
            userObject = new UserObject();
            userObjectList = new ArrayList<>();
            groupObjectList = new ArrayList<>();
            settingList = new ArrayList<>();
            memberList = new ArrayList<>();
            iconMap = new HashMap<>();
            iconMapGroup = new HashMap<>();
            iconMapGroupMember = new HashMap<>();
            hostIp = "192.168.31.241";
            hostPort = "80";
            toId = 0;
            isGroup = 0;
        }

        public void saveInfo() {
            userObject = CurrentUser.userObject;
            icon = CurrentUser.icon;
            userObjectList = CurrentUser.userObjectList;
            groupObjectList = CurrentUser.groupObjectList;
            settingList = CurrentUser.settingList;
            memberList = CurrentUser.memberList;
            iconMap = CurrentUser.iconMap;
            iconMapGroup = CurrentUser.iconMapGroup;
            iconMapGroupMember = CurrentUser.iconMapGroupMember;
            hostIp = CurrentUser.hostIp;
            hostPort = CurrentUser.hostPort;
            toId = CurrentUser.toId;
            isGroup = CurrentUser.isGroup;
        }

        public void loadInfo() {
            CurrentUser.userObject = userObject;
            CurrentUser.icon = icon;
            CurrentUser.userObjectList = userObjectList;
            CurrentUser.groupObjectList = groupObjectList;
            CurrentUser.settingList = settingList;
            CurrentUser.memberList = memberList;
            CurrentUser.iconMap = iconMap;
            CurrentUser.iconMapGroup = iconMapGroup;
            CurrentUser.iconMapGroupMember = iconMapGroupMember;
            CurrentUser.hostIp = hostIp;
            CurrentUser.hostPort = hostPort;
            CurrentUser.toId = toId;
            CurrentUser.isGroup = isGroup;
        }
    }
}