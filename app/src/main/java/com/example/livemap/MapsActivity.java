package com.example.livemap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.livemap.objects.json.MapDataSetDeserializer;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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


import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        , MarkerInfoFragment.OnFragmentInteractionListener, NewMarkerFragment.OnFragmentInteractionListener {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int MARKER_IS_PRIVATE= 1;
    private static final String DEFAULT_TITLE = "Unnamed Marker";
    private GoogleMap mMap;
    private MacActions currAction;
    private Switch mSwitchLocation; //to show or hide curr location
    private FirebaseDatabase rootNode;
    private DatabaseReference mRef;
    private String sUid;
    private MapCollection mapCollection;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Marker userLocationMarker;

    // fragment related vars
    private boolean isCustomizeFragmentDisplayed = false;
    private boolean isInfoWindowDisplayed = false;

    // Livemap objects
    User currentUser = new User("Jonny");




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        rootNode = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_maps);
        // Create new runtime instance of map fragment
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //init the  locationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500); //mils interval
        locationRequest.setFastestInterval(100); //mils interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //addMarkersFromFireBase(dataSnapshot);
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


        @Override
        protected void onStart () {
            super.onStart();
            startLocationUpdates();
        }

        @Override
        protected void onStop () {
            super.onStop();
            stopLocationUpdates();
        }


        void addMarkersFromFireBase (DataSnapshot dataSnapshot) {
            Log.w("Firebase", "dataSnapshot is: " + dataSnapshot);
            HashMap<String, JSONObject> dataSnapshotValue = (HashMap<String, JSONObject>) dataSnapshot.getValue();
            Log.w("Firebase", "dataSnapshotValue is: " + dataSnapshotValue);
            String jsonString = new Gson().toJson(dataSnapshotValue);
            Log.w("Firebase", "jsonString is: " + jsonString);
            GsonBuilder gsonBuilder = new GsonBuilder();
            MapDataSetDeserializer mapDataSetDeserializer = new MapDataSetDeserializer();
            gsonBuilder.registerTypeAdapter(MapDataSet.class, mapDataSetDeserializer);
            Gson gson = gsonBuilder.create();
            MapDataSet markers = gson.fromJson(jsonString, MapDataSet.class);
            Log.w("Firebase", "markers is: " + markers);
            if (markers != null && markers.getLocations() != null && markers.getLocations().values() != null) {
                for (MarkerLive m : markers.getLocations().values()) {
                    if (m.getMarkerOptions().getPosition() == null) continue;
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(m.getMarkerOptions().getPosition())
                            .title("Placeholder title :)")
                            .snippet(m.getMarkerOptions().getSnippet())
                            .icon(BitmapDescriptorFactory.defaultMarker //changes color
                                    (BitmapDescriptorFactory.HUE_GREEN + 20));
                    if(mMap !=null)mMap.addMarker(markerOptions);
                }
            }
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
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            //set click functions
            setMapClicks(mMap);

            enableMyLocation();

        }

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.w("lOCATION", "onLocationResult :" + locationResult.getLastLocation());
                if (mMap != null) {
                    setUserLocationMarker(locationResult.getLastLocation());
                }

            }
        };

        private void setUserLocationMarker (Location lastLocation){
            if (lastLocation == null) {
                Log.w("Location", "lastLocation is null");
            }
            LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLatitude());
            if (userLocationMarker == null) {
                //create a new marker
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                userLocationMarker = mMap.addMarker(markerOptions);

            } else { // use prev created marker
                userLocationMarker.setPosition(latLng);

            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

        }


        private void startLocationUpdates () {
            //check location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                enableMyLocation(); // if has no permission try making one
                return;
            }
            //
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        }

        private void stopLocationUpdates () {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }



            private void setMapClicks ( final GoogleMap map){
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    // called when long clicking on map
                    public void onMapLongClick(LatLng latLng) {
                        if (currAction == MacActions.ADD) {
                            addNewMarker(latLng);
                        }
                    }
                });

                // called when POI marker info window is clicked
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
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(final Marker marker) {
                        Toast.makeText(getApplicationContext(),
                                marker.getTitle() +
                                        " has been clicked",
                                Toast.LENGTH_SHORT).show();

                        // Return false to indicate that we have not consumed the event and that we wish
                        // for the default behavior to occur (which is for the camera to move such that the
                        // marker is centered and for the marker's info window to open, if it has one).
                        return false;
                    }
                });
                // called when info window is clicked
                map.setOnInfoWindowClickListener(
                        new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                MarkerLive ml = (MarkerLive)marker.getTag();
                                openMarkerInfoWindow(ml, marker);
                                //TODO open marker info window

                            }
                        });
            }

            // function for creating a new marker
            private void addNewMarker (LatLng latLng){
                // create new MarkerLive with position and send it for customization
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng);
                MarkerLive ml = new MarkerLive(sUid, markerOptions, true);
                //Marker marker = mMap.addMarker(markerOptions);
                openNewMarkerPopup(ml);
                // if returned null then user clicked cancel

            }


            // checks if there is location access, if so enables location, otherwise asks
            private void enableMyLocation () {
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
            public void onRequestPermissionsResult ( int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults){
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

            void disableMyLocation () {
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


            private void openNewMarkerPopup (MarkerLive ml){
                isCustomizeFragmentDisplayed = true;
                // Instantiate the fragment.
                NewMarkerFragment newMarkerFragment =
                        NewMarkerFragment.newInstance(ml);
                // Get the FragmentManager and start a transaction.
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();

                // Add the customizeMarkerFragment.
                fragmentTransaction.add(R.id.fragment_container,
                        newMarkerFragment).addToBackStack(null).commit();
            }

            public void closeNewMarkerPopup () {
                isCustomizeFragmentDisplayed = false;
                // Get the FragmentManager.
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Check to see if the fragment is already showing.
                NewMarkerFragment newMarkerFragment = (NewMarkerFragment) fragmentManager
                        .findFragmentById(R.id.fragment_container);
                if (newMarkerFragment != null) {
                    // Create and commit the transaction to remove the fragment.
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.remove(newMarkerFragment).commit();
                }
            }
            private void openMarkerInfoWindow (MarkerLive markerLive, Marker marker){
                isInfoWindowDisplayed = true;
                // Instantiate the fragment.
                MarkerInfoFragment markerInfoFragment =
                        MarkerInfoFragment.newInstance(markerLive, marker);
                // Get the FragmentManager and start a transaction.
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction();

                // Add the customizeMarkerFragment.
                fragmentTransaction.add(R.id.fragment_container,
                        markerInfoFragment).addToBackStack(null).commit();
            }

            public void closeMarkerInfoWindow () {
                isInfoWindowDisplayed = false;
                // Get the FragmentManager.
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Check to see if the fragment is already showing.
                MarkerInfoFragment MarkerInfoFragment = (MarkerInfoFragment) fragmentManager
                        .findFragmentById(R.id.fragment_container);
                if (MarkerInfoFragment != null) {
                    // Create and commit the transaction to remove the fragment.
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.remove(MarkerInfoFragment).commit();
                }
            }


        @Override
        public void newMarkerCreated(MarkerLive ml) {

            Log.w("MapsActivityJon", "MarkerLive is: "+ml.toString());
            currentUser.addMarkerLive(ml);
            Marker newMarker = mMap.addMarker(ml.getMarkerOptions());
            // attaching markerLive object pointer to every marker to easily access when
            // selecting marker on map
            newMarker.setTag(ml);
            //TODO add marker to database

//                    mRef = rootNode.getReference("/root/markers/" + ml.getMarkerOptions().hashCode());
//                    mRef.setValue(ml);
//                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker //changes color
//                            (BitmapDescriptorFactory.HUE_GREEN));

            closeNewMarkerPopup();
        }

        @Override
        public void newMarkerCancel() {
            closeNewMarkerPopup();
        }


        @Override
        public void markerInfoCompleteNoChange () {
            closeMarkerInfoWindow();
        }

        @Override
        public void markerInfoCompleteChange(MarkerLive ml, Marker m) {
            // TODO save changes to firebase

            // this is temp for testing, should actually be rebuilt from database
            m.setSnippet(ml.getSnippet());
            m.setTitle(ml.getTitle());
            closeMarkerInfoWindow ();

        }

        @Override
        public void markerInfoCompleteDelete(MarkerLive ml, Marker m) {
            //TODO remove from firebase
            currentUser.removeMarkerLive(ml);
            m.remove();
            closeMarkerInfoWindow ();
        }


}



