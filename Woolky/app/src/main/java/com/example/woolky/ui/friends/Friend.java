package com.example.woolky.ui.friends;

public class Friend {
    public String id;
    public String name;
    public String photoUrl;
    public String id;

    public Friend(String id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Friend(String name, String photoUrl, String id) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.id= id;
    }
}
