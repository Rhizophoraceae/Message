package com.mgs.message;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mgs.message.adapter.RecyclerViewAdapterMessage;
import com.mgs.message.data.MessageObject;
import com.mgs.message.data.UserObject;
import com.mgs.message.utils.CurrentUser;
import com.mgs.message.utils.KeyboardWatcher;
import com.mgs.message.utils.WebSocketClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements KeyboardWatcher.OnKeyboardToggleListener {
    public static boolean isClient;
    private EditText editTextMessageInput;
    private Button buttonSend;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterMessage adapter;
    private List<MessageObject> messageObjectList;
    private List<UserObject> memberList;
    private MessageReceiver receiver;
    private KeyboardWatcher keyboardWatcher;
    private ActionBar actionBar;
    private int toPosition;
    private String title;
    Handler handlerGroup = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                getMessageList();
                Log.i("chat", "群组成员列表已获取");
            }
        }
    };
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                updateUI();
                Log.i("chat", "消息列表已获取");
            }
        }
    };

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        receiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message");
        registerReceiver(receiver, intentFilter);
        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.setListener(this);
        toPosition = getIntent().getIntExtra("toPosition", 0);
        if (CurrentUser.isGroup == 1)
            title = CurrentUser.groupObjectList.get(toPosition).getGroupName();
        else
            title = CurrentUser.userObjectList.get(toPosition).getUsername();
        initUi();
        buttonSend.setOnClickListener(view -> {
            if (editTextMessageInput.getText().toString().trim().equals(""))
                Log.i("message", "消息为空");
            else {
                JSONObject messageJSON = new JSONObject();
                try {
                    messageJSON.put("username", CurrentUser.userObject.getUsername());
                    messageJSON.put("isGroup", CurrentUser.isGroup);
                    messageJSON.put("toId", CurrentUser.toId);
                    messageJSON.put("userId", CurrentUser.userObject.getUserId());
                    messageJSON.put("icon", CurrentUser.userObject.getIcon());
                    messageJSON.put("content", editTextMessageInput.getText().toString().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = messageJSON.toString();
                if (isClient) {
                    WebSocketClient.Send(message);  //发送消息
                    MessageObject tempMessageObject = new MessageObject();
                    tempMessageObject.setUsername(CurrentUser.userObject.getUsername());
                    tempMessageObject.setFromId(CurrentUser.userObject.getUserId());
                    tempMessageObject.setIcon(CurrentUser.userObject.getIcon());
                    tempMessageObject.setContent(editTextMessageInput.getText().toString().trim());
                    editTextMessageInput.setText("");
                    messageObjectList.add(tempMessageObject);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                    Log.i("message", "消息: " + tempMessageObject.getContent() + " 已发送");
                } else Toast.makeText(ChatActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuChatInfo) {
            Intent intent;
            if (CurrentUser.isGroup == 1) {
                intent = new Intent(ChatActivity.this, GroupInfoActivity.class);
            } else {
                intent = new Intent(ChatActivity.this, FriendInfoActivity.class);
            }
            intent.putExtra("toPosition", toPosition);
            startActivity(intent);
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (ChatActivity.this.getCurrentFocus() != null) {
                if (ChatActivity.this.getCurrentFocus().getWindowToken() != null) {
                    inputMethodManager.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        messageObjectList = new ArrayList<>();
        if (CurrentUser.isGroup == 1) {
            memberList = new ArrayList<>();
            getMemberList();
        } else {
            getMessageList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        keyboardWatcher.destroy();
    }

    private void initUi() {
        messageObjectList = new ArrayList<>();
        buttonSend = findViewById(R.id.buttonSend);
        editTextMessageInput = findViewById(R.id.editTextInput);
        editTextMessageInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean isOK = true;
            if (i == EditorInfo.IME_ACTION_SEND) {
                buttonSend.performClick();
                Log.i("keyPress", "发送成功");
            } else {
                isOK = false;
            }
            return isOK;
        });
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
        recyclerView = findViewById(R.id.recyclerViewMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (CurrentUser.isGroup == 1) {
            adapter = new RecyclerViewAdapterMessage(messageObjectList, CurrentUser.iconMapGroupMember.get(CurrentUser.toId));
        } else {
            adapter = new RecyclerViewAdapterMessage(messageObjectList, CurrentUser.iconMap);
        }
        recyclerView.setAdapter(adapter);
    }

    private void updateUI() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
        recyclerView = findViewById(R.id.recyclerViewMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (CurrentUser.isGroup == 1) {
            adapter = new RecyclerViewAdapterMessage(messageObjectList, CurrentUser.iconMapGroupMember.get(CurrentUser.toId));
        } else {
            adapter = new RecyclerViewAdapterMessage(messageObjectList, CurrentUser.iconMap);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messageObjectList.size() - 1);
    }

    private void getMessageList() {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", CurrentUser.userObject.getUserId() + "");
            json.put("toId", CurrentUser.toId + "");
            json.put("isGroup", CurrentUser.isGroup + "");
            Thread thread = new Thread(() -> {
                JSONArray responseJSON = httpRequest(json);
                if (responseJSON != null) {
                    try {
                        Gson gson = new Gson();
                        for (int i = 0; i < responseJSON.length(); i++) {
                            MessageObject tempMessageObject = gson.fromJson(responseJSON.get(i).toString(), MessageObject.class);
                            messageObjectList.add(tempMessageObject);
                        }
                        handler.sendEmptyMessage(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(1);
                    }
                }
                handler.sendEmptyMessage(1);
            });
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(1);
        }
    }

    private void getMemberList() {
        JSONObject json = new JSONObject();
        try {
            json.put("groupId", CurrentUser.toId + "");
            Thread thread = new Thread(() -> {
                JSONArray responseJSON = httpRequestMember(json);
                if (responseJSON != null) {
                    try {
                        HashMap<String, Bitmap> tempMap = new HashMap<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < responseJSON.length(); i++) {
                            UserObject tempUserObject = gson.fromJson(responseJSON.get(i).toString(), UserObject.class);
                            try {
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/" + tempUserObject.getIcon());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取图标" + tempUserObject.getIcon());
                                tempMap.put(tempUserObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/images/default_user.png");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取默认图标" + tempUserObject.getIcon());
                                tempMap.put(tempUserObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            }
                            memberList.add(tempUserObject);
                        }
                        CurrentUser.memberList = memberList;
                        CurrentUser.iconMapGroupMember.put(CurrentUser.toId, tempMap);
                        handlerGroup.sendEmptyMessage(0);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        handlerGroup.sendEmptyMessage(1);
                    }
                }
                handlerGroup.sendEmptyMessage(1);
            });
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
            handlerGroup.sendEmptyMessage(1);
        }
    }

    private JSONArray httpRequest(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            String userId = (String) json.get("userId");
            String toId = (String) json.get("toId");
            String isGroup = (String) json.get("isGroup");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId)
                    .add("toId", toId)
                    .add("isGroup", isGroup)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/Message")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray httpRequestMember(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            String groupId = (String) json.get("groupId");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("groupId", groupId)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/GroupMember")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onKeyboardShown(int keyboardSize) {
        Log.i("keyboard", "键盘显示");
        if (adapter.getItemCount() != 0)
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onKeyboardClosed() {

    }

    class MessageReceiver extends BroadcastReceiver {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("message", intent.getStringExtra("message"));
            MessageObject messageObject = new Gson().fromJson(intent.getStringExtra("message"), MessageObject.class);
            messageObjectList.add(messageObject);
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }
}