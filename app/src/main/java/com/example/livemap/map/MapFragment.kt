package com.example.livemap.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.livemap.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
        val latCity = 32.1046
        val lngCity = 35.1745
        val bookmarkCity =  Bookmark("Ariel-City",latCity,lngCity,true)

        val latAriel = 32.1046
        val lngAriel= 35.1745
        val bookmarkAriel =  Bookmark("Ariel-City",latAriel,lngAriel,true)
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
        googleMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
        googleMap.addMarker(MarkerOptions().position(bookmarkAriel.cord).title(bookmarkAriel.name))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bookmarkCity.cord))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(bookmarkAriel.cord))
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