package com.example.woolky.domain;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import com.example.woolky.utils.LatLngCustom;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class User implements Serializable {

    private String photoUrl;
    private String userId;
    private String userName;
    private int level, color;
    private List<String> groupsIBelong, groupsIOwn, friends;
    private LatLngCustom currentPosition;
    private ShareLocationType visibilityType;
    private Statistics stats;




    public User() {}

    public User(String userId, String userName, int level, int color, LatLngCustom currentPosition, ShareLocationType visibilityType) {
        this.userId = userId;
        this.userName = userName;
        this.level = level;
        this.color = color;
        this.currentPosition = currentPosition;
        this.visibilityType = visibilityType;

        this.groupsIBelong = new ArrayList<>();
        //groupsIBelong.add("nao vazio");
        this.groupsIOwn = new ArrayList<>();
        //groupsIOwn.add("nao vazio");
        this.friends = new ArrayList<>();


    }

    public User(String userId, String userName, int level, int color, ShareLocationType visibilityType, String photoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.level = level;
        this.color = color;
        this.visibilityType = visibilityType;
        this.photoUrl = photoUrl;

        this.groupsIBelong = new ArrayList<>();
        //groupsIBelong.add("nao vazio");
        this.groupsIOwn = new ArrayList<>();
        //groupsIOwn.add("nao vazio");

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

    public List<String> getGroupsIOwn() {
        return (groupsIOwn== null ? new ArrayList() : groupsIOwn);
    }
    public List<String> getGroupsIBelong() {

        return (groupsIBelong == null ? new ArrayList() : groupsIBelong);
    }

    public void setGroupsIOwn(List<String> groups) {
        this.groupsIOwn = groups;
    }

    public void setGroupsIBelong(List<String> groups) {
        this.groupsIBelong = groups;
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

    public void setPhotoUrl (String url) {
        this.photoUrl = url;
    }

    public String getPhotoUrl (){
        return photoUrl;
    }

    public Statistics getStats() {
        return stats;
    }

    public void setStats(Statistics stats) {
        this.stats = stats;
    }


    public void createNewGroup(String key) {

        if (groupsIOwn == null){
            groupsIOwn = new ArrayList<>();
        }

        groupsIOwn.add(key);
    }

    public void leaveGroup(String groupId) {

        groupsIBelong.remove(groupId);

    }

    public void addNewGroup(String groupId) {

        if (groupsIBelong == null){
            groupsIBelong = new ArrayList<>();
        }

        groupsIBelong.add(groupId);
    }

    public void leaveGroupIOwn(String groupId) {

        groupsIOwn.remove(groupId);
    }

    public void changeFromBelongToOwn(String groupId) {
        createNewGroup(groupId);
        leaveGroup(groupId);

    }

    public void updateStepsAndDistance (int steps, double distance ) {

        stats.updateStepsAndDistance(steps,distance);
    }

    /*
    Por semana
     */
    public int getTotalNumberSteps(int week) {

        return stats.getTotalNumberSteps(week);


    }
}
