package com.mgs.message.utils;

import android.graphics.Bitmap;

import com.mgs.message.data.GroupObject;
import com.mgs.message.data.Setting;
import com.mgs.message.data.UserObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentUser {
    public static UserObject userObject = new UserObject();
    public static Bitmap icon;
    public static List<UserObject> userObjectList = new ArrayList<>();
    public static List<GroupObject> groupObjectList = new ArrayList<>();
    public static List<Setting> settingList = new ArrayList<>();
    public static List<UserObject> memberList = new ArrayList<>();
    public static HashMap<String, Bitmap> iconMap = new HashMap<>();
    public static HashMap<String, Bitmap> iconMapGroup = new HashMap<>();
    public static HashMap<Integer, HashMap<String, Bitmap>> iconMapGroupMember = new HashMap<>();

    /*
        本机服务器地址
        public static String hostIp = "192.168.31.241";
        public static String hostPort = "80";
    */

    // 服务器地址
    public static String hostIp = "192.168.123.15";
    public static String hostPort = "8080";
//    public static String hostIp = "172.27.10.186";
//    public static String hostPort = "80";
    public static int toId = 0;
    public static int isGroup = 0;
}
