package com.example.teamfive.Notification;

public class SendData {

    private String info;
    private String name;

    public SendData(String info, String name) {
        this.info = info;
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
