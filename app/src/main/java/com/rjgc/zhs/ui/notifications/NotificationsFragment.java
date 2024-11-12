package com.rjgc.zhs.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rjgc.zhs.R;
import com.rjgc.zhs.adapter.RecyclerViewAdapterSettings;
import com.rjgc.zhs.data.Setting;
import com.rjgc.zhs.databinding.FragmentNotificationsBinding;
import com.rjgc.zhs.utils.CurrentUser;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private RecyclerView recyclerView;
    private RecyclerViewAdapterSettings adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getSettingList();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getSettingList() {
        CurrentUser.settingList = new ArrayList<>();
        Setting setting = new Setting(R.drawable.ic_baseline_account_circle_24, R.string.setting_userinfo);
        CurrentUser.settingList.add(setting);
        setting = new Setting(R.drawable.ic_baseline_info_24, R.string.setting_systemInfo);
        CurrentUser.settingList.add(setting);
        recyclerView = binding.recyclerViewSettings;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapterSettings(CurrentUser.settingList);
        recyclerView.setAdapter(adapter);
    }
}