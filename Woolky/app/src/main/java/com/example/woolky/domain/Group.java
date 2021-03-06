package com.example.woolky.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group  implements Serializable  {

    //assumir k o dono eh pos 0
    private List<String> members;
    private String groupName;
    private boolean visible;
    private String id;

    public Group() {}

    public Group(String name, String owner, String key) {
        this.groupName = name;
        members = new ArrayList<>();
        members.add(owner);
        visible = true;
        id=key;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getId(){
        return id;
    }

    public void setMembers(List<String> list) {
        members = list;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String name) {
        groupName = name;
    }

    public boolean getVisible(){
        return visible;
    }

    public void setVisibles(boolean v) {
        visible = v;
    }

    public String getOwnerId() {
        return members.get(0);
    }

    public void addMember(String id) {
        members.add(id);
    }

    public int getNumberMembers() {
        return members.size();
    }

    public void deleteOwner() {
        members.remove(0);
    }

    public void deleteMember(String memberId) {
        members.remove(memberId);
    }

    public boolean hasMember(String id) {
        return members.contains(id);
    }
}
