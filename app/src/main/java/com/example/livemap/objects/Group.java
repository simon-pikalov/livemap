package com.example.livemap.objects;

import com.example.livemap.utils.FirebaseFunctionalities;
import com.example.livemap.utils.MarkerOwner;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Group implements MarkerOwner {
    private User currentUser;
    private String adminId;
    // only storing ids here
    private HashMap<String,String> users;
    private HashMap<String,MarkerLive> markers;
    private String name;
    private String id;
    // key to access friend methods
    public static final class KeyClass { private KeyClass() {} }
    private static final Group.KeyClass groupClassKey = new Group.KeyClass();

    // for serialization in Firebase
    public Group(){}
    // group is created using admin user
    public Group(User.KeyClass k, FirebaseFunctionalities FireFunc, User currentUser, User admin, String n){
        users = new HashMap<>();
        users.put(admin.getId(), admin.getId());
        adminId = admin.getId();
        name = n;
        id = UUID.randomUUID().toString();

        //Sould be done through firebase
        //admin.joinGroup(this);
        this.currentUser = currentUser;
    }

    public Group addUser(User u){
        users.put(u.getId(), u.getId());
        u.joinGroup( this);
        return this;
    }

    public Group removeUser(User u){
        users.remove(u.getId());
        u.exitGroup(this);
        return this;
    }
    public boolean hasUser(String uid){return users.containsKey(uid);}

    @Exclude
    public List<User> getUsersList(){
        if(users==null){
            throw new NullPointerException("group's users not properly loaded from database");
        }
        LinkedList<User> userObjects= new LinkedList<>();
        for(String userId: users.values()){
            User userObject = currentUser.getPal(userId);
            if(userObject!=null)userObjects.add(userObject);
            else throw new RuntimeException("Group: user in group, but not in list of users");

        }
        return  userObjects;
    }

    public String getId(){return id;}
    public void setId(String id){this.id = id;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}



    @Override
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getId(), ml);}
    @Override
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getId());}
}
