package com.rjgc.zhs.ui.home;

import androidx.lifecycle.ViewModel;

import com.rjgc.zhs.data.User;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private List<User> userList = null;

    public HomeViewModel() {
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}