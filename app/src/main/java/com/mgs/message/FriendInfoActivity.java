package com.mgs.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mgs.message.data.UserObject;
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

public class FriendInfoActivity extends AppCompatActivity {
    private UserObject friend;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Intent intent = new Intent(FriendInfoActivity.this, MainActivity.class);
                startActivity(intent);
                ToastSender.send(getApplicationContext(), "好友已删除");
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendinfo);
        int toPosition = getIntent().getIntExtra("toPosition", 0);
        friend = CurrentUser.userObjectList.get(toPosition);
        ImageView imageViewIcon = this.findViewById(R.id.imageViewIcon);
        imageViewIcon.setImageBitmap(CurrentUser.iconMap.get(friend.getIcon()));
        TextView textViewUsername = this.findViewById(R.id.textViewUsername);
        textViewUsername.setText(friend.getUsername());
        TextView textViewEmail = this.findViewById(R.id.textViewEmail);
        if (friend.getEmail() == null)
            textViewEmail.setText("用户未填写电子邮箱");
        else
            textViewEmail.setText(friend.getEmail());
        TextView textViewSignature = this.findViewById(R.id.textViewSignature);
        if (friend.getSignature() == null)
            textViewSignature.setText("用户未填写签名");
        else
            textViewSignature.setText(friend.getSignature());
        Button button = this.findViewById(R.id.buttonDelete);
        button.setOnClickListener(view -> deleteFriend(CurrentUser.userObject.getUserId(), friend.getUserId()));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("好友信息");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void deleteFriend(int userId, int deleteId) {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("deleteId", deleteId);
            Thread thread = new Thread(() -> {
                JSONObject responseJSON = httpRequest(json);
                if (responseJSON != null) {
                    try {
                        int code = responseJSON.getInt("code");
                        handler.sendEmptyMessage(code);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            int userId = json.getInt("userId");
            int deleteId = json.getInt("deleteId");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId + "")
                    .add("deleteId", deleteId + "")
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/DeleteFriend")
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
