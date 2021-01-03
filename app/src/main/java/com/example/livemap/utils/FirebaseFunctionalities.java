package com.example.livemap.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.livemap.objects.Group;
import com.example.livemap.objects.MarkerLive;
import com.example.livemap.objects.MessageLive;
import com.example.livemap.objects.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
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
    private DatabaseReference mMessagesNode;
    private FirebaseInteractionListener mListener;
    //interface to delegate instructions to main activity
    public interface FirebaseInteractionListener{
        void receiveGroupJoinInvitation(MessageLive message);
    }

    private final String USERS_PATH = "/users/";
    private final String GROUPS_PATH = "/groups/";
    private final String MESSAGE_BOX = "/message_box/";
    //this path contains for every user and for every group its members and groups he is in, respectively
    private final String GROUP_USER_RELATION_PATH = "/group_user/";
    private final String MARKERS_PATH = "/markers/";

    public FirebaseFunctionalities(Context context,GoogleMap map){
        mMap=map;
        mListener = (FirebaseInteractionListener)context;
        mDatabase=FirebaseDatabase.getInstance();
        mGroupsNode = mDatabase.getReference(GROUPS_PATH);
        mUsersNode = mDatabase.getReference(USERS_PATH);
        mGroupUserNode = mDatabase.getReference(GROUP_USER_RELATION_PATH);
        mMessagesNode = mDatabase.getReference(MESSAGE_BOX);


        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference refToThisUser = mUsersNode.child(uid);

        setListenerForCurrentUser();


//      code below needs user to not be null (taken care of in setListenerForCurrentUser();)
        mUser.attachFirebaseFuntionalities(this);
        addListenerForGroupsOfCurrentUser();
        addMessageListener();
        mUserMarkersNode = mDatabase.getReference(MARKERS_PATH+ mUser.getId()+"/");

        setListenerForMarkers();

    }

    private void setListenerForCurrentUser(){
        // TODO fix issue with loading user drom db
        String uid = FirebaseAuth.getInstance().getUid();
        Log.w("JonFirebase", "user Id is: "+uid);
        //Try to get this user from firebase
        DatabaseReference refToThisUser = mUsersNode.child(uid);
//        Log.w("JonFirebase", "got reference: "+refToThisUser);
//        refToThisUser.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                Log.w("JonFirebase", "tried to get user from db: "+snapshot);
//                mUser = snapshot.getValue(User.class);
//                if(mUser!=null){
//                    Log.w("JonFirebase", "successfuly restored user: "+mUser.toString());
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w("JonFirebase", error.getMessage());
//            }
//        });

        if(mUser==null){
            Log.w("JonFirebase", "creating new user");
            mUser=new User("Jonny", uid,"0502253227");
            refToThisUser.setValue(mUser);
        }

        /////////////TEST DATA START/////////////////////////
        //add a couple test users
        String[] names = {"Bob","Jim","Tommy"};
        String[] phones = {"111","222","333"};
        String[] ids = {"test3asdfd42","test123dsd2","test3sdf3233"};
        LinkedList<User> usersTest = new LinkedList<>();
        for(int i=0; i<3;++i ){
            usersTest.add(new User(names[i],ids[i],phones[i]));
        }

        DatabaseReference refToUser = mUsersNode;
        for(int i=0; i<3;++i ){
            refToUser.child(ids[i]).setValue(usersTest.get(i));
        }
        // create a group for testing
        Group group1 = mUser.createGroup("TestGroup1");
        // otherwise id is random
        group1.setId("test12988412821");
        addGroupToFirebase(group1);
        //add users on group in firebase, but not on device
        DatabaseReference groupUserRef = mGroupUserNode.child(group1.getId());
        for(int i=0; i<3;++i ){
            groupUserRef.child(ids[i]).setValue(1);
        }
        // add current user too
        mGroupUserNode.child(mUser.getId()).child(group1.getId()).setValue(1);


        // do the same but don't add user this time, send invitation instead
        Group group2 = mUser.createGroup("TestGroupWithMessage");

        // otherwise id is random
        group2.setId("test1292352321");
        Log.w("JonFirebase", "adding group to firebase");
        //add group id to users list of groups
        DatabaseReference groupsRefUser = mGroupUserNode.child(mUser.getId());

        /////////////TEST END/////////////////////////

    }



    private void sendGroupInvitationByPhoneNumber(String phoneNumber){
        Query userId = mDatabase.getReference(USERS_PATH).equalTo(phoneNumber);
    }

    // gets ids of user's groups and adds listeners for them
    private void addListenerForGroupsOfCurrentUser(){
        DatabaseReference refToGroupUserRelation = mGroupUserNode.child(mUser.getId());
        refToGroupUserRelation.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String groupId = (String)snapshot.getKey();
                Log.w("JonFirebase", "setting listener for group with id: " + groupId);
                //if user has not joined yet, add listener that will also join user to the group
                if (!mUser.hasGroup(groupId)) {
                    setListenerForGroup(groupId);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase", "restore groups got group  id entered on child removed");
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
                //Log.w("JonFirebase", "Got snapshot: " +snapshot);
                MarkerLive markerLive = (MarkerLive) snapshot.getValue(MarkerLive.class);
                //Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
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
                //Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
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

    // loads group and joins user to it
    private void setListenerForGroup(String groupId){

        // first a listener for the properties of the group: name, etc.
        mGroupsNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.w("JonFirebase", "onChildAdded Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "attempting restore of group from firebase: " + group.toString());
                if (!mUser.hasGroup(group.getId())) {
                    // order is important
                    group.setCurrentUser(mUser);
                    group.addUser(mUser.getId());
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.w("JonFirebase", "onChildChanged Got group snapshot: " +snapshot);
//                Group group = (Group)snapshot.getValue(Group.class);
//                Log.w("JonFirebase", "Got group: " + group.toString());
//                //if user was removed, then remove group from user's list of groups
//                if (mUser.hasGroup(group.getId())&&!group.hasUser(mUser.getId())) {
//                    mUser.exitGroup(group);
//                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase", "onChildRemoved Got group snapshot: " +snapshot);
                Group group = (Group)snapshot.getValue(Group.class);
                Log.w("JonFirebase", "Got group: " + group.toString());
                //if it's a new marker, create it
                if (mUser.hasGroup(group.getId())) {
                    group.removeUser(mUser.getId());
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
        //set listeners for users who are in the group
        DatabaseReference refToGroupUserRelation = mGroupUserNode.child(groupId);
        refToGroupUserRelation.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userId = snapshot.getKey();
                Log.w("JonFirebase", "adding listener for group user (if needed): " + userId);
                if(userId!=mUser.getId()){
                    addListenerForGroupUser(userId);
                }
                mUser.getGroupById(groupId).addUser(userId);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase", "attempting to remove user from group");
                String removedUser = snapshot.getKey();
                mUser.getGroupById(groupId).removeUser(removedUser);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //TODO finish1

    // listen to a single user who is in given group
    private void addListenerForGroupUser(String userId){
        DatabaseReference refToUser = mUsersNode.child(userId);
        refToUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.w("JonFirebase","attempting user restore from snapshot: "+snapshot);
                User user = snapshot.getValue(User.class);
                // TODO update user listener for getting updates
                // check if user is already stored on deivce
                if(!mUser.isPalsWith(user.getId())){
                    mUser.addPal(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMessageListener(){
        DatabaseReference ref = mMessagesNode.child(mUser.getId());
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageLive receivedMessage = snapshot.getValue(MessageLive.class);
                //delegate response to main activity
                mListener.receiveGroupJoinInvitation(receivedMessage);
                // clean up
                ref.child(snapshot.getKey()).removeValue();
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
        Log.w("JonFirebase", "adding group to firebase");
        //add group id to users list of groups
        DatabaseReference groupsRefUser = mGroupUserNode.child(mUser.getId());
        groupsRefUser. child(group.getId()).setValue(1);

        // set value of group and its user relations
        DatabaseReference markerRef = mGroupsNode.child(group.getId());
        markerRef.setValue(group);
        //upload all users of this group to database
        DatabaseReference usersRefGroup = mGroupUserNode.child(group.getId());
        for(String uid: group.getUserIdList()){
            usersRefGroup.child(uid).setValue(1);
        }
    }

    // just adds the group to the relations, listeners will take care of the rest
    public void joinGroup(String groupId){
        mGroupUserNode.child(mUser.getId()).child(groupId).setValue(groupId);
    }
    public void removeUserFromGroup(String userId, String groupId){
        mGroupUserNode.child(groupId).child(userId).removeValue();
    }
    public void sendGroupInvitation(String userId, Group group){
        MessageLive msg = new MessageLive(mUser, group);
        mMessagesNode.child(userId).child(msg.getId()).setValue(msg);
    }

    // gets user after restoring him from firebase
    public User getCurrentUser(){
        if(mUser==null)throw new NullPointerException("user was not created before attemting to get it");
        return mUser;
    }

}
