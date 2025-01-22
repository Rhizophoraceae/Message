package com.mgs.message.ui.dashboard;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mgs.message.adapter.RecyclerViewAdapterGroups;
import com.mgs.message.utils.ToastSender;
import com.mgs.message.data.GroupObject;
import com.mgs.message.databinding.FragmentDashboardBinding;
import com.mgs.message.utils.CurrentUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterGroups adapter;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Log.i("home", CurrentUser.groupObjectList.toString());
                updateUI();
                Log.i("home", "群组列表已获取");
            } else {
                Log.i("home", "群组列表获取失败");
            }
        }
    };

    Handler handlerAddGroup = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                binding.editTextGroupId.setText("");
                getGroupList();
                ToastSender.send(getActivity().getApplicationContext(), "加入成功");
            } else {
                binding.editTextGroupId.setText("");
                ToastSender.send(getActivity().getApplicationContext(), "加入失败");
            }
        }
    };

    Handler handlerCreateGroup = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                binding.editTextGroupName.setText("");
                getGroupList();
                ToastSender.send(getActivity().getApplicationContext(), "创建成功");
            } else {
                binding.editTextGroupName.setText("");
                ToastSender.send(getActivity().getApplicationContext(), "创建失败");
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonAdd.setOnClickListener(view -> addGroup(CurrentUser.userObject.getUserId(), binding.editTextGroupId.getText().toString().trim()));
        binding.buttonCreate.setOnClickListener(view -> createGroup(CurrentUser.userObject.getUserId(), binding.editTextGroupName.getText().toString().trim()));

        getGroupList();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void addGroup(int userId, String addId) {
        if (!addId.equals("")) {
            JSONObject json = new JSONObject();
            try {
                json.put("userId", userId);
                json.put("addId", addId);
                Thread thread = new Thread(() -> {
                    JSONObject responseJSON = httpRequestAddGroup(json);
                    if (responseJSON != null) {
                        try {
                            int code = responseJSON.getInt("code");
                            handlerAddGroup.sendEmptyMessage(code);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastSender.send(getActivity().getApplicationContext(), "群组编号不能为空");
        }
    }

    private void createGroup(int userId, String groupName) {
        if (!groupName.equals("")) {
            JSONObject json = new JSONObject();
            try {
                json.put("userId", userId);
                json.put("groupName", groupName);
                Thread thread = new Thread(() -> {
                    JSONObject responseJSON = httpRequestCreateGroup(json);
                    if (responseJSON != null) {
                        try {
                            int code = responseJSON.getInt("code");
                            handlerCreateGroup.sendEmptyMessage(code);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastSender.send(getActivity().getApplicationContext(), "群组名不能为空");
        }
    }

    private void getGroupList() {
        JSONObject json = new JSONObject();
        List<GroupObject> groupObjectList = new ArrayList<>();
        try {
            json.put("userId", CurrentUser.userObject.getUserId() + "");
            Thread thread = new Thread(() -> {
                JSONArray responseJSON = httpRequest(json);
                if (responseJSON != null) {
                    try {
                        Gson gson = new Gson();
                        for (int i = 0; i < responseJSON.length(); i++) {
                            GroupObject tempGroupObject = gson.fromJson(responseJSON.get(i).toString(), GroupObject.class);
                            try {
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/" + tempGroupObject.getIcon());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取图标" + tempGroupObject.getIcon());
                                CurrentUser.iconMapGroup.put(tempGroupObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/images/default_group.png");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取默认图标" + tempGroupObject.getIcon());
                                CurrentUser.iconMapGroup.put(tempGroupObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            }
                            groupObjectList.add(tempGroupObject);
                        }
                        CurrentUser.groupObjectList = groupObjectList;
                        handler.sendEmptyMessage(0);
                    } catch (JSONException | IOException e) {
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

    private void updateUI() {
        recyclerView = binding.recyclerViewGroups;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapterGroups(CurrentUser.groupObjectList, CurrentUser.iconMapGroup);
        recyclerView.setAdapter(adapter);
    }

    private JSONObject httpRequestAddGroup(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            int userId = json.getInt("userId");
            String addId = (String) json.get("addId");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId + "")
                    .add("addId", addId)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/JoinGroup")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject httpRequestCreateGroup(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            int userId = json.getInt("userId");
            String groupName = (String) json.get("groupName");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId + "")
                    .add("groupName", groupName)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/CreateGroup")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray httpRequest(JSONObject json) {
        String ip = CurrentUser.hostIp;
        String port = CurrentUser.hostPort;
        try {
            String userId = (String) json.get("userId");
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", userId)
                    .build();
            Request request = new Request.Builder()
                    .url("http://" + ip + ":" + port + "/MessageServer/Groups")
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return new JSONArray(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}