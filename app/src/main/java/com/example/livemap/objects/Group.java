package com.example.livemap.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Group {

    private String adminHash;
    private HashMap<String,User> users;
    private String name;
    private String id;
    // key to access friend methods
    public static final class KeyClass { private KeyClass() {} }
    private static final Group.KeyClass groupClassKey = new Group.KeyClass();

    // group is created using admin user
    public Group(User.KeyClass k, User admin, String n){
        users = new HashMap<>();
        users.put(admin.getID(), admin);
        adminHash = admin.getID();
        name = n;
        id = UUID.randomUUID().toString();
        admin.joinGroup(groupClassKey,this);
    }

    public Group addUser(User u){
        users.put(u.getID(), u);
        u.joinGroup(groupClassKey, this);
        return this;
    }

    public Group removeUser(User u){
        users.remove(u.getID());
        u.exitGroup(groupClassKey, this);
        return this;
    }

    public List<User> getUsers(){return new ArrayList<>(users.values());}
    public String getID(){return id;}
    public void setName(String name){this.name=name;}
    public String getName(){return name;}
}
