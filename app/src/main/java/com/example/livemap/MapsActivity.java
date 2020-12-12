package com.example.livemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.livemap.objects.json.MapDataSetDeserializer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.livemap.objects.*;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;
    private MacActions currAction;
    private Switch mSwitchLocation; //to show or hide curr location
    FirebaseDatabase rootNode;
    DatabaseReference mRef;
    String sUid;
    MapCollection mapCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootNode = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_maps);
        // Create ne runtime instance of map fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        // add map fragment to frame layout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);
        currAction = MacActions.ADD;
        sUid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // the user hash of the current user
        mSwitchLocation = (Switch) findViewById(R.id.switchLocation);
        mRef = rootNode.getReference("/root/markers/");
        mapCollection = new MapDataSet();

        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w("Firebase", "dataSnapshot is: " + dataSnapshot);

                HashMap<String, JSONObject> dataSnapshotValue = (HashMap<String, JSONObject>) dataSnapshot.getValue();
                Log.w("Firebase", "dataSnapshotValue is: " + dataSnapshotValue);
                String jsonString = new Gson().toJson(dataSnapshotValue);
                Log.w("Firebase", "jsonString is: " + jsonString);
                GsonBuilder gsonBuilder = new GsonBuilder();
                MapDataSetDeserializer mapDataSetDeserializer = new MapDataSetDeserializer();
                gsonBuilder.registerTypeAdapter(MapDataSet.class, mapDataSetDeserializer);
                Gson gson = gsonBuilder.create();
                MapDataSet markers  = gson.fromJson(jsonString, MapDataSet.class);
                Log.w("Firebase", "markers is: " + markers);
                if(markers != null&&markers.getLocations()!=null&&markers.getLocations().values()!=null){
                    for(MarkerLive m :markers.getLocations().values()){
                        if(m.getMarkerOptions().getPosition()==null) continue;
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(m.getMarkerOptions().getPosition())
                                .title("Placeholder title :)")
                                .snippet(m.getMarkerOptions().getSnippet())
                                .icon(BitmapDescriptorFactory.defaultMarker //changes color
                                        (BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(markerOptions);
                }

               }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });

        mSwitchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    enableMyLocation();
                } else {
                    disableMyLocation();
                }
            }
        });

    }


    void addMarkersFromFireBase(){

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        double latAriel = 32.1046;
        double lngAriel = 35.1745;


        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latAriel, lngAriel)));
        LatLng arielCord = new LatLng(latAriel, lngAriel);

        //Add ground overlay example
        GroundOverlayOptions homeOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.android))
                .position(arielCord, 100);
        mMap.addGroundOverlay(homeOverlay);

        //for long click function to work (and poi)
        setMapLongClick(mMap);
        //setPoiClick(mMap);
        // enable current location
        enableMyLocation();
        setInfoWindowClickToEditBookmark(mMap);

    }

    // handles the creation of markers
    private void setMapLongClick(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                if (currAction == MacActions.ADD) {
                    MarkerLive ml = new MarkerLive();
                    String description = String.format(Locale.getDefault(),
                            "Lat: %1$.5f, Long: %2$.5f",
                            latLng.latitude,
                            latLng.longitude) + "\n Vasia Was here";
                    // create custom marker

                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title("Placeholder title :)")
                            .snippet(description);

                    ml = new MarkerLive(sUid, markerOptions, true);
                    mRef = rootNode.getReference("/root/markers/" + ml.getMarkerOptions().hashCode());
                    mRef.setValue(ml);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker //changes color
                            (BitmapDescriptorFactory.HUE_GREEN));
                    map.addMarker(markerOptions);
                }


            }
        });
        // this is the same as setOnMapLongClickListener, except there is poi info
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                // shows info window immediately
                poiMarker.showInfoWindow();
            }
        });
    }

    // checks if there is location access, if so enables location, otherwise asks
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    //invoked on requestPermissions in enableMyLocation
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    void disableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    // listens to clicks on the info window, when clicked opens edit fragment
    private void setInfoWindowClickToEditBookmark(GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (marker.getTag() == "custom") {
                            Toast.makeText(getApplicationContext(), "Info window clicked!",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}