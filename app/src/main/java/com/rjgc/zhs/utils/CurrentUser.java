package com.rjgc.zhs.utils;

import android.graphics.Bitmap;

import com.rjgc.zhs.data.Group;
import com.rjgc.zhs.data.Setting;
import com.rjgc.zhs.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentUser {
    public static User user = new User();
    public static Bitmap icon;
    public static List<User> userList = new ArrayList<>();
    public static List<Group> groupList = new ArrayList<>();
    public static List<Setting> settingList = new ArrayList<>();
    public static List<User> memberList = new ArrayList<>();
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
