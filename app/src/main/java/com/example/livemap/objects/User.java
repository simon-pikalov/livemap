package com.example.livemap.objects;

public class User {

    String userHash;
    boolean isAdmin;


    public User(String userName) {
        this.isAdmin = false;
        this.userHash = userName;
    }


    public User(String userName, boolean isAdmin) {
        this.userHash = userName;
        this.isAdmin = isAdmin;
    }

    public String getUserHash() {
        return userHash;
    }

    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "userHash='" + userHash + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
