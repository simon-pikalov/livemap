package com.example.livemap.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User {

    private String name;
    private boolean isAdmin;
    private String id;
    private String phone;
    private HashMap<String, MarkerLive> markers;
    private HashMap<String, Group> groups;



    public User(String userName) {
        this.isAdmin = false;
        name = userName;
        this.id = UUID.randomUUID().toString();
        markers = new HashMap<>();
        groups = new HashMap<>();
    }


    // this class' purpose is to allow only a user to create a group
    public static final class KeyClass { private KeyClass() {} }
    private static final KeyClass userClassKey = new KeyClass();


    public User(String name, String phone) {

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.phone = phone;
        markers = new HashMap<>();
        groups = new HashMap<>();
    }


    public MarkerLive getMarkerLive(String id){return markers.get(id);}
    public boolean hasMarker(String id){
        if(markers.containsKey(id)) return true;
        return false;
    }
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getHash(), ml);}
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getHash());}
    public String getID() {
        return id;
    }
    // create and join group
    public Group createGroup(String groupName){
        return new Group(userClassKey, this, groupName);
    }

    public void joinGroup(Group.KeyClass k,Group g){
        groups.put(g.getID(),g);
    }
    public void exitGroup(Group.KeyClass k,Group g){
        groups.remove(g.getID());
    }
    public List<Group> getGroups(){return new ArrayList<Group>(groups.values());}
    public void setID(String id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID='" + id + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
