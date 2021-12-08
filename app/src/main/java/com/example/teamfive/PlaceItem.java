package com.example.teamfive;

import java.util.ArrayList;

public class PlaceItem {
    String key;
    String name;
    String info;
    double latitude;
    double longitude;
    ArrayList<String> tag;
    String time;
    int visit;

    public PlaceItem() {}

    public PlaceItem(String key, String name, String info, double latitude, double longitude, ArrayList<String> tag, String time, int visit) {
        this.key = key;
        this.name = name;
        this.info = info;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tag = tag;
        this.time = time;
        this.visit = visit;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
