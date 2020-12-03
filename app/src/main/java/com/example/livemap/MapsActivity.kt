package com.example.livemap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import objects.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latCity = 32.1046
        val lngCity = 35.1745
        val bookmarkCity =  Bookmark("Ariel-City",latCity,lngCity,true)

        val latAriel = 32.1046
        val lngAriel= 35.1745
        val bookmarkAriel =  Bookmark("Ariel-City",latAriel,lngAriel,true)
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
        mMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
        mMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bookmarkCity.cord))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bookmarkAriel.cord))

    }
}