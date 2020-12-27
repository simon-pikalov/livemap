package com.example.livemap.objects;

import java.util.HashMap;
import java.util.UUID;

public class User {

    String name;
    boolean isAdmin;
    String id;
    HashMap<String, MarkerLive> markers;
    HashMap<String, Group> groups;

    public User(String userName) {
        this.isAdmin = false;
        this.id = UUID.randomUUID().toString();
        markers = new HashMap<>();
        groups = new HashMap<>();
    }

    public MarkerLive getMarkerLive(String id){return markers.get(id);}
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getMarkerHash(), ml);}
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getMarkerHash());}
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
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
                "ID='" + id + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
