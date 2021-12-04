package com.example.woolky.domain;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private String userId;
    private String userName;
    private int level, color;
    private List<String> groups, friends;
    private LatLngCustom currentPosition;
    private ShareLocationType visibilityType;

    public User() {}

    public User(String userId, String userName, int level, int color, LatLngCustom currentPosition, ShareLocationType visibilityType) {
        this.userId = userId;
        this.userName = userName;
        this.level = level;
        this.color = color;
        this.currentPosition = currentPosition;
        this.visibilityType = visibilityType;

        this.groups = new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public User(String userId, String userName, int level, int color, ShareLocationType visibilityType) {
        this.userId = userId;
        this.userName = userName;
        this.level = level;
        this.color = color;
        this.visibilityType = visibilityType;

        this.groups = new ArrayList<>();
        this.friends = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public LatLngCustom getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(LatLngCustom currentPosition) {
        this.currentPosition = currentPosition;
    }

    public ShareLocationType getVisibilityType() {
        return visibilityType;
    }

    public void setVisibilityType(ShareLocationType visibilityType) {
        this.visibilityType = visibilityType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
