package com.example.livemap.map

import android.content.ContentValues.TAG
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.livemap.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import objects.Bookmark

class MapFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        //get instance of database
        val fireDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()

        val latAriel = 32.1046
        val lngAriel= 35.1745
        val bookmarkAriel =  Bookmark("Ariel-City",latAriel,lngAriel,true)

        val databaseRef: DatabaseReference = fireDatabase.getReference(
            "/test/bookmarks/arielBookmark")
        databaseRef.child("test")
            .child("bookmarks")
            .child("arielBookmark")
            .setValue(bookmarkAriel)

        val bookmarkListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                //TODO make constructor for bookmark from POJO (format firebase returns)
                val bookmark = dataSnapshot.getValue<Bookmark>()
                // [START_EXCLUDE]
                bookmark?.let {
                    googleMap.addMarker(MarkerOptions().position(it.cord).title(it.name))
                    googleMap.addMarker(MarkerOptions().position(it.cord).title(it.name))
                     googleMap.moveCamera(CameraUpdateFactory.newLatLng(it.cord))
                }
                // [END_EXCLUDE]
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // [START_EXCLUDE]
                Toast.makeText(activity, "Failed to load coordinate.",
                    Toast.LENGTH_SHORT).show()
                // [END_EXCLUDE]
            }
        }

        databaseRef.addValueEventListener(bookmarkListener)
        // [END post_value_event_listener]


 //       googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
//        googleMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
//        googleMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bookmarkAriel.cord))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}