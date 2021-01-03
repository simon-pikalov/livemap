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

public class User implements MarkerOwner {

    private String name;
    private boolean isAdmin;
    private String id;
    private String phone;
    private FirebaseFunctionalities fireFunc;
    private HashMap<String, MarkerLive> markers;
    private HashMap<String, Group> groups;
    private HashMap<String, User> mapPals;

    //TODO make a class for all things general
    @Exclude
    public FirebaseFunctionalities getFireFunc(){return fireFunc;}
    // For restoration from Firebase
    public User(){
        groups = new HashMap<>();
        markers = new HashMap<>();
        mapPals = new HashMap<>();
    }

    public User(String name, String id) {
        this();
        this.isAdmin = false;
        this.name = name;
        this.id = id;
    }

    //fake user, used for testing only
    public User(String name){
        this(name, UUID.randomUUID().toString());
    }
    public User(String name, String id, String phone) {
        this(name, id);
        this.phone = phone;
    }
    // needed for listening for changes on users
    public void attachFirebaseFuntionalities(FirebaseFunctionalities fireFunc){this.fireFunc =fireFunc;}

    // this class' purpose is to allow only a user to create a group
    public static final class KeyClass { private KeyClass() {} }
    @Exclude
    private static final KeyClass userClassKey = new KeyClass();



    public MarkerLive getMarkerLive(String id){return markers.get(id);}
    public boolean hasMarker(String id){
        if(markers.containsKey(id)) return true;
        return false;
    }
    public void addMarkerLive(MarkerLive ml){markers.put(ml.getId(), ml);}
    public void removeMarkerLive(MarkerLive ml){markers.remove(ml.getId());}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // create and join group
    public Group createGroup(String groupName){
        return new Group(userClassKey, fireFunc,this,this, groupName);
    }

    public void joinGroup(Group g){
        Log.w("UserObject", "added group"+g.toString()+"to user");
        groups.put(g.getId(),g);
    }
    public void exitGroup(Group g){
        groups.remove(g.getId());
    }

    public boolean isPalsWith(String pal){return mapPals.containsKey(pal);}
    public User getPal(String id){return mapPals.get(id);}
    public void addPal(User pal){ mapPals.put(pal.getId(),pal);}
    public void removePal(User pal){
        mapPals.remove(pal.getId());
    }

    public boolean hasGroup(String groupId){return groups.containsKey(groupId);}
    public List<Group> getGroups(){return new ArrayList<>(groups.values());}
    public Group getGroupById(String id){return groups.get(id);}

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
        return "User{name=" +name+", "+
                "ID='" + id + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
