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
import java.util.concurrent.CountDownLatch;

public class FirebaseFunctionalities {
    private GoogleMap mMap;
    private User mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserMarkersNode;
    private DatabaseReference mGroupsNode;
    private DatabaseReference mUsersNode;
    private DatabaseReference mGroupUserNode;

    private final String USERS_PATH = "/users/";
    private final String GROUPS_PATH = "/groups/";
    //this path contains for every user and for every group its members and groups he is in, respectively
    private final String GROUP_USER_RELATION_PATH = "/group_user/";
    private final String MARKERS_PATH = "/markers/";

    public FirebaseFunctionalities(GoogleMap map){
        mMap=map;

        mDatabase=FirebaseDatabase.getInstance();
        mGroupsNode = mDatabase.getReference(GROUPS_PATH);
        mUsersNode = mDatabase.getReference(USERS_PATH);
        mGroupUserNode = mDatabase.getReference(GROUP_USER_RELATION_PATH);



        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference refToThisUser = mUsersNode.child(uid);

        setListenerForCurrentUser();

        // TODO fix issue with loading user drom db
        // if this user has no entry in database, create it



//      code below needs user to not be null (taken care of in setListenerForCurrentUser();)
        mUser.attachFirebaseFuntionalities(this);
        addListenerForCurrentUserGroups();

        mUserMarkersNode = mDatabase.getReference(MARKERS_PATH+ mUser.getId()+"/");

        setListenerForMarkers();

    }

    private void setListenerForCurrentUser(){
        String uid = FirebaseAuth.getInstance().getUid();
        Log.w("JonFirebase", "user Id is: "+uid);
        //Try to get this user from firebase
        DatabaseReference refToThisUser = mUsersNode.child(uid);
        Log.w("JonFirebase", "got reference: "+refToThisUser);
        refToThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.w("JonFirebase", "tried to get user from db: "+snapshot);
                mUser = snapshot.getValue(User.class);
                if(mUser!=null){
                    Log.w("JonFirebase", "successfuly restored user: "+mUser.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("JonFirebase", error.getMessage());
            }
        });

        if(mUser==null){
            Log.w("JonFirebase", "creating new user");
            mUser=new User("Jonny", uid);
            refToThisUser.setValue(mUser);
        }

    }

    private void addListenerForCurrentUserGroups(){
        // this adds the ids of the groups a user is in, restore them if not already present
        DatabaseReference refToGroupUserRelation = mGroupUserNode.child(mUser.getId());
        refToGroupUserRelation.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String groupId = (String)snapshot.getValue(String.class);
                if(groupId!="updateTrigger") {
                    Log.w("JonFirebase", "Got snapshot: " + snapshot);
                    Log.w("JonFirebase", "User restore - Got group  id: " + groupId);
                    //if user doesn't have group, add it by setting listeners for it
                    if (!mUser.hasGroup(groupId)) {
                        setListenerForGroup(groupId);
                    }
                }
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

        // first a listener for the properties of the group: name, etc.
        mGroupsNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "onChildAdded Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());

                if (!mUser.hasGroup(group.getId())) {
                    mUser.joinGroup(group);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "onChildChanged Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if user was removed, then remove group from user's list of groups
                if (mUser.hasGroup(group.getId())&&!group.hasUser(mUser.getId())) {
                    mUser.exitGroup(group);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase", "onChildRemoved Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if it's a new marker, create it
                if (mUser.hasGroup(group.getId())) {
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
        // listener for markers of this group
        DatabaseReference groupMarkersNode = mDatabase.getReference(MARKERS_PATH+groupId+"/");
        groupMarkersNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.w("JonFirebase", "Got snapshot: " +snapshot);
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                Log.w("JonFirebase", "Got group marker: " + markerLive.toString());
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
                Log.w("JonFirebase", "Got group marker: " + markerLive.toString());
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
        // listener for users of this group
        DatabaseReference groupUsersNode = mDatabase.getReference(GROUP_USER_RELATION_PATH+groupId+"/");
        groupMarkersNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Group group = mUser.getGroupById(groupId);
                Log.w("JonFirebase", "Got snapshot: " +snapshot);
                String userId = (String) snapshot.getValue();
                Log.w("JonFirebase", "Got group member id: " + userId);
                //if it's a new marker, create it
                if (!group.hasUser(userId)) {
                    group.addUser(mUser.getPal(userId));
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
//                Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
//                //if it's a new marker, create it
//                if (mUser.hasMarker(markerLive.getId())) {
//                    markerLive.restoreOwner(mUser);
//                    Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
//                    markerLive.attachMarker(marker);
//                }
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
        //upload all users of this group to database
        DatabaseReference usersRef = mGroupUserNode.child(group.getId());
        for(String uid: group.getUserIdList()){
            usersRef.setValue(uid);
        }
        //add listener for this group, its markers and its users
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
