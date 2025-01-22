package com.mgs.message.ui.home;

import androidx.lifecycle.ViewModel;

import com.mgs.message.data.UserObject;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private List<UserObject> userObjectList = null;

    public HomeViewModel() {
    }

    public List<UserObject> getUserList() {
        return userObjectList;
    }

    public void setUserList(List<UserObject> userObjectList) {
        this.userObjectList = userObjectList;
    }
}