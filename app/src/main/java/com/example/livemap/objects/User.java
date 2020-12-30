package com.example.livemap.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class User {

    String name;
    boolean isAdmin;
    String id;
    HashMap<String, MarkerLive> markers;
    HashMap<String, Group> groups;

    // this class' purpose is to allow only a user to create a group
    public static final class KeyClass { private KeyClass() {} }
    private static final KeyClass groupConstructorKey = new KeyClass();

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

    // create and join group
    public Group createGroup(String groupName){
        return new Group(groupConstructorKey, this, groupName);
    }

    public void joinGroup(Group g){groups.put(g.getID(),g);}
    public void exitGroup(Group g){groups.remove(g.getID());}
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

    @Override
    public String toString() {
        return "User{" +
                "ID='" + id + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
