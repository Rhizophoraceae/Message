package com.mgs.message.ui.home;

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
import com.mgs.message.adapter.RecyclerViewAdapterFriends;
import com.mgs.message.data.UserObject;
import com.mgs.message.utils.ToastSender;
import com.mgs.message.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterFriends adapter;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Log.i("home", CurrentUser.userObjectList.toString());
                updateUI();
                Log.i("home", "好友列表已获取");
            } else {
                Log.i("home", "好友列表获取失败");
            }
        }
    };

    Handler handlerAddFriend = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                binding.editTextFriendId.setText("");
                getFriendList();
                ToastSender.send(getActivity().getApplicationContext(), "添加成功");
            } else {
                binding.editTextFriendId.setText("");
                ToastSender.send(getActivity().getApplicationContext(), "添加失败");
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.buttonAdd.setOnClickListener(view -> addFriend(CurrentUser.userObject.getUserId(), binding.editTextFriendId.getText().toString().trim()));

        getFriendList();

        return root;
    }

    private void addFriend(int userId, String addId) {
        if (!addId.equals("")) {
            JSONObject json = new JSONObject();
            try {
                json.put("userId", userId);
                json.put("addId", addId);
                Thread thread = new Thread(() -> {
                    JSONObject responseJSON = httpRequestAddFriend(json);
                    if (responseJSON != null) {
                        try {
                            int code = responseJSON.getInt("code");
                            handlerAddFriend.sendEmptyMessage(code);

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
            ToastSender.send(getActivity().getApplicationContext(), "好友编号不能为空");
        }
    }

    private void getFriendList() {
        JSONObject json = new JSONObject();
        List<UserObject> userObjectList = new ArrayList<>();
        try {
            json.put("userId", CurrentUser.userObject.getUserId() + "");
            Thread thread = new Thread(() -> {
                JSONArray responseJSON = httpRequest(json);
                if (responseJSON != null) {
                    try {
                        Gson gson = new Gson();
                        for (int i = 0; i < responseJSON.length(); i++) {
                            UserObject tempUserObject = gson.fromJson(responseJSON.get(i).toString(), UserObject.class);
                            try {
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/" + tempUserObject.getIcon());
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取图标" + tempUserObject.getIcon());
                                CurrentUser.iconMap.put(tempUserObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            } catch (Exception e) {
                                e.printStackTrace();
                                URL url = new URL("http://" + CurrentUser.hostIp + ":" + CurrentUser.hostPort + "/MessageServer/images/default_user.png");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                Log.i("icon", "获取默认图标" + tempUserObject.getIcon());
                                CurrentUser.iconMap.put(tempUserObject.getIcon(), BitmapFactory.decodeStream(connection.getInputStream()));
                            }
                            userObjectList.add(tempUserObject);
                        }
                        CurrentUser.userObjectList = userObjectList;
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
        recyclerView = binding.recyclerViewFriends;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapterFriends(CurrentUser.userObjectList, CurrentUser.iconMap);
        recyclerView.setAdapter(adapter);
    }

    private JSONObject httpRequestAddFriend(JSONObject json) {
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
                    .url("http://" + ip + ":" + port + "/MessageServer/AddFriend")
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
                    .url("http://" + ip + ":" + port + "/MessageServer/Friends")
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