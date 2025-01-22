package com.mgs.message;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mgs.message.service.MessageService;
import com.mgs.message.utils.ToastSender;
import com.mgs.message.data.UserObject;
import com.mgs.message.utils.CurrentUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserinfoActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private ImageView imageViewIcon;
    private TextView textViewId;
    private EditText editTextUsername;
    private EditText editTextSignature;
    private EditText editTextEmail;
    private File photoFile;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                CurrentUser.userObject.setSignature(editTextSignature.getText().toString().trim());
                CurrentUser.userObject.setEmail(editTextEmail.getText().toString().trim());
                ToastSender.send(getApplicationContext(), "保存成功");
            } else {
                ToastSender.send(getApplicationContext(), "保存失败");
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //此处是跳转的result回调方法
            if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                photoFile = uriToFile(result.getData().getData());
                String url = "http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/ChangeIcon";
                uploading(url, photoFile);
                CurrentUser.icon = BitmapFactory.decodeFile(photoFile.getPath());
                CurrentUser.iconMap.put(CurrentUser.userObject.getIcon(), CurrentUser.icon);
                imageViewIcon.setImageURI(result.getData().getData());
                Log.i("userInfo", "返回成功");
            } else {
                Log.i("userInfo", "返回失败");
            }
        });
        imageViewIcon = this.findViewById(R.id.imageViewIcon);
        imageViewIcon.setImageBitmap(CurrentUser.icon);
        imageViewIcon.setOnClickListener(view -> openAlbum());
        textViewId = this.findViewById(R.id.textViewId);
        textViewId.setText(CurrentUser.userObject.getUserId() + "");
        editTextUsername = this.findViewById(R.id.editTextUsername);
        editTextUsername.setText(CurrentUser.userObject.getUsername());
        editTextSignature = this.findViewById(R.id.editTextSignature);
        editTextSignature.setText(CurrentUser.userObject.getSignature());
        editTextEmail = this.findViewById(R.id.editTextEmail);
        editTextEmail.setText(CurrentUser.userObject.getEmail());
        Button buttonSave = this.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(view -> saveUserInfo(CurrentUser.userObject.getUserId(), editTextSignature.getText().toString().trim(), editTextEmail.getText().toString().trim()));
        Button buttonLogout = this.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(view -> logout());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("个人信息");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        CurrentUser.userObject = new UserObject();
        CurrentUser.icon = null;
        CurrentUser.userObjectList = new ArrayList<>();
        CurrentUser.groupObjectList = new ArrayList<>();
        CurrentUser.settingList = new ArrayList<>();
        CurrentUser.iconMap = new HashMap<>();
        CurrentUser.iconMapGroup = new HashMap<>();
        CurrentUser.iconMapGroupMember = new HashMap<>();
        CurrentUser.toId = 0;
        CurrentUser.isGroup = 0;
        Intent intentStopService = new Intent(UserinfoActivity.this, MessageService.class);
        stopService(intentStopService);
        Intent intentLogin = new Intent(UserinfoActivity.this, MainActivity.class);
        startActivity(intentLogin);
    }

    private void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intentActivityResultLauncher.launch(intent);
    }

    private File uriToFile(Uri uri) {
        File file = null;
        if (uri == null) return null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = this.getContentResolver();
            String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)
                    + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));

            try {
                InputStream is = contentResolver.openInputStream(uri);
                File cache = new File(this.getCacheDir().getAbsolutePath(), displayName);
                FileOutputStream fos = new FileOutputStream(cache);
                FileUtils.copy(is, fos);
                file = cache;
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public void uploading(String url, File file) {

        //创建RequestBody封装参数
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/png"));
        //创建MultipartBody,给RequestBody进行设置
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", CurrentUser.userObject.getUserId() + "")
                .addFormDataPart("icon", file.getName(), fileBody)
                .build();
        //创建Request
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();

        //创建okhttp对象
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        //上传完图片,得到服务器反馈数据
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("changeIcon", "上传失败: uploadMultiFile() e=" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i("changeIcon", "上传成功: uploadMultiFile() response=" + response.body().string());
            }
        });
    }

    private void saveUserInfo(int userId, String signature, String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("signature", signature);
            json.put("email", email);
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
            String signature = (String) json.get("signature");
            String email = (String) json.get("email");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId + "")
                    .add("signature", signature)
                    .add("email", email)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/ChangeUserInfo")
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
