package com.mgs.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.mgs.message.data.User;
import com.mgs.message.utils.CurrentUser;
import com.mgs.message.utils.ToastSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsername;
    EditText editTextPassword;
    Button buttonLogin;
    Button buttonRegister;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                Intent intentSave = new Intent();
                intentSave.setAction("save");
                sendBroadcast(intentSave);
                ToastSender.send(getApplicationContext(), "登录成功");
            } else if (msg.what == 1) {
                ToastSender.send(getApplicationContext(), "用户名或密码错误");
            } else {
                ToastSender.send(getApplicationContext(), "登录失败");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = this.findViewById(R.id.editTextUsername);
        editTextPassword = this.findViewById(R.id.editTextPassword);
        buttonLogin = this.findViewById(R.id.buttonLogin);
        buttonRegister = this.findViewById(R.id.buttonRegister);

        editTextPassword.setOnEditorActionListener((textView, i, keyEvent) -> {
            buttonLogin.performClick();
            Log.i("keyPress", "登录成功");
            return false;
        });

        buttonLogin.setOnClickListener(view -> {
            if (!editTextUsername.getText().toString().trim().equals("") && !editTextPassword.getText().toString().trim().equals("")) {
                Login(editTextUsername.getText().toString().trim(), editTextPassword.getText().toString().trim());
            } else {
                ToastSender.send(getApplicationContext(), "用户名或密码不能为空");
            }
        });

        buttonRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (LoginActivity.this.getCurrentFocus() != null) {
                if (LoginActivity.this.getCurrentFocus().getWindowToken() != null) {
                    inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (CurrentUser.user.getUserId() != 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void Login(String username, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            Thread thread = new Thread(() -> {
                JSONObject responseJSON = httpRequest(json);
                if (responseJSON != null) {
                    Log.i("json", responseJSON.toString());
                    CurrentUser.user = new Gson().fromJson(responseJSON.toString(), User.class);
                    Log.i("user", CurrentUser.user.toString());
                    if (CurrentUser.user.getUserId() == -1) {
                        handler.sendEmptyMessage(1);
                    } else {
                        handler.sendEmptyMessage(0);
                    }
                } else {
                    handler.sendEmptyMessage(2);
                }
            });
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject httpRequest(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            String username = (String) json.get("username");
            String password = (String) json.get("password");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/Login")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
