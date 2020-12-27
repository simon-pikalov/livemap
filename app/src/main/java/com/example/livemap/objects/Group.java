package com.example.livemap.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Group {

    private String adminHash;
    private HashMap<String,User> users;
    private String name;
    private String id;
    // group is created using admin user
    public Group(User admin, String n){
        users = new HashMap<>();
        users.put(admin.getID(), admin);
        adminHash = admin.getID();
        name = n;
        id = UUID.randomUUID().toString();
    }

    public void addUser(User u){
        users.put(u.getID(), u);
    }

    public void removeUser(User u){
        users.remove(u.getID());
    }
    public String getID(){return id;}
}
