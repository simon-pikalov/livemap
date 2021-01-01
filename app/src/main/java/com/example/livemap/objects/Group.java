package com.example.livemap.objects;

import com.example.livemap.utils.MarkerOwner;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Group implements MarkerOwner {

    private String adminHash;
    private HashMap<String,User> users;
    private HashMap<String,MarkerLive> markers;
    private String name;
    private String id;
    // key to access friend methods
    public static final class KeyClass { private KeyClass() {} }
    private static final Group.KeyClass groupClassKey = new Group.KeyClass();

    // for serialization in Firebase
    public Group(){}
    // group is created using admin user
    public Group(User.KeyClass k, User admin, String n){
        users = new HashMap<>();
        users.put(admin.getId(), admin);
        adminHash = admin.getId();
        name = n;
        id = UUID.randomUUID().toString();
        admin.joinGroup(groupClassKey,this);
    }

    public Group addUser(User u){
        users.put(u.getId(), u);
        u.joinGroup(groupClassKey, this);
        return this;
    }

    public Group removeUser(User u){
        users.remove(u.getId());
        u.exitGroup(groupClassKey, this);
        return this;
    }

    @Exclude
    public List<User> getUsers(){return new ArrayList<>(users.values());}
    public String getId(){return id;}
    public void setId(String id){this.id = id;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    @Override
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getId(), ml);}
    @Override
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getId());}
}
