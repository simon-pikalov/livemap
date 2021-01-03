package com.example.livemap.objects;

import android.util.Log;

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
    public Group(){
        users=new HashMap<>();
        markers=new HashMap<>();
    }
    // group is created using admin user
    public Group(User.KeyClass k, User currentUser, String adminId, String name){
        if(currentUser==null) throw new NullPointerException("tried to create group with current user == null");
        users = new HashMap<>();
        this.adminId = adminId;
        this.name = name;

        //should be done manually when restoring
        this.currentUser =currentUser;

        // note that id is generated randomly
        id = UUID.randomUUID().toString();

    }
    //IMPORTANT: must set current user after restoring from DB
    @Exclude
    public void setCurrentUser(User cUser){currentUser=cUser;}

    //TODO clean up after separating current user and session related data

    public Group addUser(String userId){
        if(userId==currentUser.getId()){
            currentUser.joinGroup(this);
        }
        users.put(userId, userId);
        return this;
    }
    public Group removeUser(String userId){
        if(userId==currentUser.getId()){
            currentUser.exitGroup(this);
        }
        users.remove(userId);
        return this;
    }
    public boolean hasUser(String uid){return users.containsKey(uid);}

    @Exclude
    // users are stores in user object, get them using their ids
    public List<User> getUsersList(){
        LinkedList<User> userObjects= new LinkedList<>();
        Log.w("GroupObject","attempting to get users for ids: "+users.values());
        for(String userId: users.values()){
            User userObject = currentUser.getPal(userId);
            if(userObject!=null) userObjects.add(userObject);
            else throw new RuntimeException("Group: user in group not loaded properly");
        }
        return  userObjects;
    }
    @Exclude
    public List<String> getUserIdList(){return new ArrayList<>(users.values());}

    public String getId(){return id;}
    public void setId(String id){this.id = id;}
    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String toString(){
        return "{name: "+name +","+"id: "+id+"}";
    }
    public String getAdminId(){return adminId;}
    public void setAdminId(String id){adminId=id;}


    @Override
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getId(), ml);}
    @Override
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getId());}
}
