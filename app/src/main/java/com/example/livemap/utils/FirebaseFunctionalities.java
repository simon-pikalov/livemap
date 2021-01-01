package com.example.livemap.utils;

import android.util.Log;

import com.example.livemap.objects.MarkerLive;
import com.example.livemap.objects.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseFunctionalities {
    private GoogleMap mMap;
    private User mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mMarkersNode;
    private final String MARKERS_PATH = "/root/markers/";

    public FirebaseFunctionalities(GoogleMap map, User user){
        mMap=map;
        mUser = user;
        mDatabase=FirebaseDatabase.getInstance();

        mMarkersNode = mDatabase.getReference(MARKERS_PATH);

        mMarkersNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("JonFirebase", "shapshot: " + dataSnapshot.getKey());

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    MarkerLive markerLive = (MarkerLive) markerSnapshot.getValue(MarkerLive.class);
                    Log.w("JonFirebase", "Got markerLive: " + markerLive.toString());
                    //to avoid creating same marker twice
                    if (markerLive != null && !mUser.hasMarker(markerLive.getId())) {
                        markerLive.restoreOwner(mUser);
                        Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                        markerLive.attachMarker(marker);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }






    public void addMarkerToFirebase(MarkerLive markerLive){
        // get reference to path of current marker
        DatabaseReference markerRef = mMarkersNode.child(markerLive.getId());
        // set its value
        markerRef.setValue(markerLive);

//        markerLive.setIcon(BitmapDescriptorFactory.defaultMarker
//                 (BitmapDescriptorFactory.HUE_GREEN)); //changes color

    }
    public void removeMarkerFromFirebase(MarkerLive markerLive){
        DatabaseReference markerRef = mMarkersNode.child(markerLive.getId());
        markerRef.removeValue();
    }
}
