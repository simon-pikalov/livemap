package com.example.livemap.objects;

import com.google.firebase.database.Exclude;

import java.util.UUID;

public class MessageLive {
    private String senderName;
    private String senderId;
    private String groupId;
    private String groupName;
    private String id;


    public MessageLive(){}

    // used for reconstructing message from firebase
    public MessageLive(String senderName, String senderId, String groupId, String groupName, String id){
        this.senderName=senderName;
        this.senderId=senderId;
        this.groupId=groupId;
        this.groupName=groupName;
        this.id = id;
    }

    // not that this constructor generates a random id
    public MessageLive(User sender, Group group){
        this.senderName=sender.getName();
        this.senderId=sender.getId();
        this.groupId=group.getId();
        this.groupName=group.getName();
        this.id = UUID.randomUUID().toString();
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
    public String getId() {
        return groupName;
    }
}
