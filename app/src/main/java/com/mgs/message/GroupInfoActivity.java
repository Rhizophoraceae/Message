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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgs.message.adapter.RecyclerViewAdapterGroupMembers;
import com.mgs.message.data.GroupObject;
import com.mgs.message.utils.CurrentUser;
import com.mgs.message.utils.ToastSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class GroupInfoActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private ImageView imageViewIcon;
    private EditText editTextGroupName;
    private EditText editTextDescribe;
    private int toPosition;
    private GroupObject groupObject;
    private File photoFile;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Intent intent = new Intent(GroupInfoActivity.this, MainActivity.class);
                startActivity(intent);
                ToastSender.send(getApplicationContext(), "已退出群组");
            }
        }
    };

    Handler handlerGroupInfo = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                CurrentUser.groupObjectList.get(toPosition).setGroupName(editTextGroupName.getText().toString().trim());
                CurrentUser.groupObjectList.get(toPosition).setDescribe(editTextDescribe.getText().toString().trim());
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
        setContentView(R.layout.activity_groupinfo);
        toPosition = getIntent().getIntExtra("toPosition", 0);
        groupObject = CurrentUser.groupObjectList.get(toPosition);
        imageViewIcon = this.findViewById(R.id.imageViewIcon);
        imageViewIcon.setImageBitmap(CurrentUser.iconMapGroup.get(groupObject.getIcon()));
        TextView textViewId = this.findViewById(R.id.textViewId);
        textViewId.setText(groupObject.getGroupId() + "");
        editTextGroupName = this.findViewById(R.id.editTextGroupName);
        editTextGroupName.setText(groupObject.getGroupName());
        editTextDescribe = this.findViewById(R.id.editTextDescribe);
        editTextDescribe.setText(groupObject.getDescribe());
        Button button = this.findViewById(R.id.buttonDelete);
        button.setOnClickListener(view -> deleteGroup(CurrentUser.userObject.getUserId(), groupObject.getGroupId()));
        Button buttonSave = this.findViewById(R.id.buttonSave);
        Log.i("groupInfo", "ownerId: " + groupObject.getOwnerId());
        if (CurrentUser.userObject.getUserId() == groupObject.getOwnerId()) {
            imageViewIcon.setOnClickListener(view -> openAlbum());
            buttonSave.setOnClickListener(view -> saveGroupInfo(groupObject.getGroupId(), editTextGroupName.getText().toString().trim(), editTextDescribe.getText().toString().trim()));
        } else {
            editTextGroupName.setEnabled(false);
            editTextDescribe.setEnabled(false);
            buttonSave.setEnabled(false);
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerViewGroupMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapterGroupMembers adapter = new RecyclerViewAdapterGroupMembers(CurrentUser.memberList, CurrentUser.iconMapGroupMember.get(groupObject.getGroupId()));
        recyclerView.setAdapter(adapter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("群组信息");
        }

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //此处是跳转的result回调方法
            if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
                photoFile = uriToFile(result.getData().getData());
                String url = "http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/ChangeGroupIcon";
                uploading(url, photoFile);
                CurrentUser.iconMapGroup.put(groupObject.getIcon(), BitmapFactory.decodeFile(photoFile.getPath()));
                imageViewIcon.setImageURI(result.getData().getData());
                Log.i("userInfo", "返回成功");
            } else {
                Log.i("userInfo", "返回失败");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
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
                .addFormDataPart("groupId", groupObject.getGroupId() + "")
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

    private void saveGroupInfo(int groupId, String groupName, String describe) {
        JSONObject json = new JSONObject();
        try {
            json.put("groupId", groupId);
            json.put("groupName", groupName);
            json.put("describe", describe);
            Thread thread = new Thread(() -> {
                JSONObject responseJSON = httpRequestGroupInfo(json);
                if (responseJSON != null) {
                    try {
                        int code = responseJSON.getInt("code");
                        handlerGroupInfo.sendEmptyMessage(code);

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

    private void deleteGroup(int userId, int deleteId) {
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
                    .url("http://" + ip + ":" + port + "/MessageServer/DeleteGroup")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject httpRequestGroupInfo(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            int groupId = json.getInt("groupId");
            String groupName = (String) json.get("groupName");
            String describe = (String) json.get("describe");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("groupId", groupId + "")
                    .add("groupName", groupName)
                    .add("describe", describe)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/ChangeGroupInfo")
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
