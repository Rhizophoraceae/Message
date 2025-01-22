package com.mgs.message.data;

public class MessageEvent {

    private String message; //网络数据内容
    private int number;  //网络数据分类

    public MessageEvent(int number, String message) {
        this.message = message;
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}