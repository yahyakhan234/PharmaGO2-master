package com.fyp.classes;

public class online_user {
    private String UID;
    private String UToken;

    public online_user() {
    }

    public online_user(String UID, String UToken) {
        this.UID = UID;
        this.UToken = UToken;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUToken() {
        return UToken;
    }

    public void setUToken(String UToken) {
        this.UToken = UToken;
    }
}
