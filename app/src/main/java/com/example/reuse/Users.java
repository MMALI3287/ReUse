package com.example.reuse;

public class Users {
    private String displayName;
    private String profilePhotoUrl;

    public Users(){
    }

    public Users(String displayName, String profilePhotoUrl) {
        this.displayName = displayName;
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }


    public Users(String displayName) {
        this.displayName = displayName;
        this.profilePhotoUrl = "null";
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
