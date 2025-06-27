package com.mgs.message.utils;

import android.util.Log;

import com.mgs.message.ChatActivity;
import com.mgs.message.data.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {
    public WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        //开启连接
        Log.d("WebSocketClient", "onOpen" + "成功连接到：" + getRemoteSocketAddress());
        EventBus.getDefault().post(new MessageEvent(2, "onOpen：" + getRemoteSocketAddress()));
        ChatActivity.isClient = true;

    }

    @Override
    public void onMessage(String message) {
        //接受消息
        Log.d("WebSocketClient", "onMessage" + message);
        EventBus.getDefault().post(new MessageEvent(1, getRemoteSocketAddress() + "：" + message));

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        //断开连接
        Log.d("WebSocketClient", "onClose");
        EventBus.getDefault().post(new MessageEvent(2, "onClose：" + reason));

    }

    @Override
    public void onError(Exception ex) {
        //发生错误
        Log.d("WebSocketClient", "onError");
        EventBus.getDefault().post(new MessageEvent(2, "onError：" + ex.toString()));

    }

    private static WebSocketClient webSocketClient;

    //发起连接
    public static boolean connect(String ip, String port, int uid) {
        if (webSocketClient != null) {
            Release();
        }
        if (webSocketClient == null) {
            URI uri = URI.create("ws://" + ip + ":" + port + "/MessageServer/websocket/" + uid);
            webSocketClient = new WebSocketClient(uri);
        }
        try {
            webSocketClient.connectBlocking();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void Release() {
        Close();
        webSocketClient = null;
    }

    public static void Close() {
        if (webSocketClient == null) return;
        if (!webSocketClient.isOpen()) return;
        try {
            webSocketClient.closeBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //发送消息
    public static void Send(String string) {
        if (webSocketClient == null) return;
        if (!webSocketClient.isOpen())
            Reconnect();
        try {
            webSocketClient.send(string);
        } catch (WebsocketNotConnectedException e) {
            e.printStackTrace();
        }
    }

    public static boolean Reconnect() {
        if (webSocketClient == null) return false;
        if (webSocketClient.isOpen()) return true;
        try {
            webSocketClient.reconnectBlocking();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
