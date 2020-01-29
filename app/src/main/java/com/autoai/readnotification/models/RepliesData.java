package com.autoai.readnotification.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RepliesData implements Serializable {
    String id,name,number,message;
    Bitmap photo;
    boolean isAdded;

    public RepliesData(String id, String name, String number, String message) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.message = message;
    }

    public RepliesData(String id, String name, String number, String message,boolean isAdded) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.message = message;
        this.isAdded = isAdded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }
}
