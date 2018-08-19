package com.example.admin.metis;

import android.net.Uri;

public class User {

    private String userID;
    private String name;
    private Uri image;

    public User(String userID, String name, Uri image){

        this.userID = userID;
        this.name = name;
        this.image = image;

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
