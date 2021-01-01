package com.example.livemap.utils;

import android.util.Log;

import com.example.livemap.objects.MapDataSet;
import com.example.livemap.objects.MarkerLive;
import com.example.livemap.objects.User;
import com.example.livemap.objects.json.MapDataSetDeserializer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;

public class FirebaseFunctionalities {
    private GoogleMap mMap;
    private User mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRootNode;
    private final String MARKERS_PATH = "/root/markers/";

    public FirebaseFunctionalities(GoogleMap map, User user){
        mMap=map;
        mUser = user;
        mDatabase=FirebaseDatabase.getInstance();
        mRootNode = mDatabase.getReference(MARKERS_PATH);
        mRootNode.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addMarkersFromFireBase(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    //public void setMap(GoogleMap map){mMap = map;}

    public void addMarkersFromFireBase (DataSnapshot dataSnapshot) {
        Log.w("JonFirebase", "***DATA SHAPSHOT START***");
        Log.w("JonFirebase", "shapshot key: "+dataSnapshot.getKey());
        Log.w("JonFirebase","***DATA SHAPSHOT END***");

        for(DataSnapshot markerSnapshot: dataSnapshot.getChildren()){
            MarkerLive markerLive = (MarkerLive)markerSnapshot.getValue(MarkerLive.class);
            Log.w("JonFirebase", "Got markerLive: "+markerLive.toString());
            //to avoid creating same marker twice
            if(!mUser.hasMarker(markerLive.getHash())) {
                mUser.addMarkerLive(markerLive);
                Marker marker = mMap.addMarker(markerLive.getMarkerOptions());
                markerLive.attachMarker(marker);
            }

        }


//        Log.w("Firebase", "dataSnapshot is: " + dataSnapshot);
//        HashMap<String, JSONObject> dataSnapshotValue = (HashMap<String, JSONObject>) dataSnapshot.getValue();
//        Log.w("Firebase", "dataSnapshotValue is: " + dataSnapshotValue);
//        String jsonString = new Gson().toJson(dataSnapshotValue);
//        Log.w("Firebase", "jsonString is: " + jsonString);
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        MapDataSetDeserializer mapDataSetDeserializer = new MapDataSetDeserializer();
//        gsonBuilder.registerTypeAdapter(MapDataSet.class, mapDataSetDeserializer);
//        Gson gson = gsonBuilder.create();
//        MapDataSet markers = gson.fromJson(jsonString, MapDataSet.class);
//        Log.w("Firebase", "markers is: " + markers);
//        if (markers != null && markers.getLocations() != null && markers.getLocations().values() != null) {
//            for (MarkerLive m : markers.getLocations().values()) {
//                if (m.getPosition() == null) continue;
//                MarkerOptions markerOptions = new MarkerOptions()
//                        .position(m.getPosition())
//                        .title("Placeholder title :)")
//                        .snippet(m.getSnippet())
//                        .icon(BitmapDescriptorFactory.defaultMarker //changes color
//                                (BitmapDescriptorFactory.HUE_GREEN + 20));
//                if(mMap !=null)mMap.addMarker(markerOptions);
//            }
//        }
    }

    public void addMarkerToFirebase(MarkerLive markerLive){
        // get reference to path of current marker
        DatabaseReference markerRef = mRootNode.child(markerLive.getHash());
        // set its value
        markerRef.setValue(markerLive);

//        markerLive.setIcon(BitmapDescriptorFactory.defaultMarker
//                 (BitmapDescriptorFactory.HUE_GREEN)); //changes color

    }
}
