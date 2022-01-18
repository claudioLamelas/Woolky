package com.example.woolky.ui.friends;

public class Friend {
    public String id;
    public String name;
    public String photoUrl;

    public Friend(String name, String id)
    {
        this.id = id;
        this.name = name;
    }
    public Friend(String id, String name, String photoUrl) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
