package com.example.livemap.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.MarkerLive;
import com.example.livemap.objects.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FirebaseFunctionalities {
    private GoogleMap mMap;
    private User mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserMarkersNode;
    private DatabaseReference mGroupsNode;
    private DatabaseReference mUsersNode;

    private final String USERS_PATH = "/users/";
    private final String GROUPS_PATH = "/groups/";
    private final String GROUP_USER_RELATION_PATH = "/group_user/";
    private final String MARKERS_PATH = "/markers/";

    public FirebaseFunctionalities(GoogleMap map){
        mMap=map;

        mDatabase=FirebaseDatabase.getInstance();
        mGroupsNode = mDatabase.getReference(GROUPS_PATH);
        mUsersNode = mDatabase.getReference(USERS_PATH);

        setListenerForCurrentUser();

        mUserMarkersNode = mDatabase.getReference(MARKERS_PATH+mUser.getId()+"/");

        setListenerForMarkers();

    }

    private void setListenerForCurrentUser(){
        String uid = FirebaseAuth.getInstance().getUid();

        //Try to get this user from firebase, otherwise create new user
        DatabaseReference refToThisUser = mUsersNode.child(uid);

        refToThisUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // didn't have entry for this user, created new user
        if(mUser==null){
            Log.w("JonFirebase", "Created new user");
            mUser=new User("New User",uid);
            mUser.attachFirebaseFuntionalities(this);
            refToThisUser.setValue(mUser);
        }

    }
    private void setListenerForUserGroups(){
        List<Group> groupList = mUser.getGroups();
        for(Group group: groupList){
            setListenerForGroup(group.getId());
        }
    }

    private void setListenerForMarkers(){
        mUserMarkersNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "Got snapshot: " +snapshot);
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
                //if it's a new marker, create it
                if (!mUser.hasMarker(markerLive.getId())) {
                    markerLive.restoreOwner(mUser);
                    Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                    markerLive.attachMarker(marker);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
                //if it's a new marker, create it
                if (mUser.hasMarker(markerLive.getId())) {
                    markerLive.restoreOwner(mUser);
                    Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                    markerLive.attachMarker(marker);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }


    private void setListenerForGroup(String groupId){
        mGroupsNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "onChildAdded Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if it's a new marker, create it
                if (!mUser.hasGroup(group)) {
                    mUser.joinGroup(group);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "onChildChanged Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if user was removed, then remove group from user's list of groups
                if (mUser.hasGroup(group)&&!group.hasUser(mUser.getId())) {
                    mUser.exitGroup(group);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase", "onChildRemoved Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if it's a new marker, create it
                if (mUser.hasGroup(group)) {
                    mUser.exitGroup(group);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        DatabaseReference groupMarkersNode = mDatabase.getReference(MARKERS_PATH+groupId+"/");
        groupMarkersNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "Got snapshot: " +snapshot);
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
                //if it's a new marker, create it
                if (!mUser.hasMarker(markerLive.getId())) {
                    markerLive.restoreOwner(mUser);
                    Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                    markerLive.attachMarker(marker);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
                //if it's a new marker, create it
                if (mUser.hasMarker(markerLive.getId())) {
                    markerLive.restoreOwner(mUser);
                    Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                    markerLive.attachMarker(marker);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    public void setUserListener(String id){
        DatabaseReference ref = mUsersNode.child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public User getCurrentUser(){
        if(mUser==null)throw new NullPointerException("user was not created before attemting to get it");
        return mUser;
    }



    public void addMarkerToFirebase(MarkerLive markerLive){
        // get reference to path of current marker
        DatabaseReference markerRef = mUserMarkersNode.child(markerLive.getId());
        // set its value
        markerRef.setValue(markerLive);

//        markerLive.setIcon(BitmapDescriptorFactory.defaultMarker
//                 (BitmapDescriptorFactory.HUE_GREEN)); //changes color
    }
    public void removeMarkerFromFirebase(MarkerLive markerLive){
        DatabaseReference markerRef = mUserMarkersNode.child(markerLive.getId());
        int numOfCopies = markerLive.getNumOfCopies();
        // if marker has only one copy on this device
        if(numOfCopies==1){
            markerRef.removeValue();
        }
        else{
            markerRef.child("numOfCopies").setValue(numOfCopies-1);
        }
    }

    // used only when creating a group
    public void addGroupToFirebase(Group group){
        // get reference to path of current group and set value
        DatabaseReference markerRef = mGroupsNode.child(group.getId());
        markerRef.setValue(group);
        //add listner for this group and its markers
        setListenerForGroup(group.getId());

    }




//    public void attachUserListener(String id){
//
//        // get reference to user path and listen for changes
//        DatabaseReference userRef = FirebaseDatabase.gchild(USERS_PATH).child(id);
//        userRef.addValueEventListener(new ValueEventListener(){
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//    }
}
