package com.example.livemap.map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.livemap.MainActivity
import com.example.livemap.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import objects.Bookmark
import java.util.concurrent.TimeUnit

class MapFragment : Fragment() {

    //auth variables
    private lateinit var mPhoneNumber: EditText
    private lateinit var mCode: EditText
    private lateinit var mSend: Button
    private  lateinit var mCallBacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks



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
        //init the buttons
        mSend = view?.findViewById(R.id.sendVerificationButton)!!
        mCode = view?.findViewById(R.id.code)!!
        mPhoneNumber = view?.findViewById(R.id.phoneNumber)!!
        //init the buttons  end
        
        mSend.setOnClickListener {view -> startPhoneNumberVerification() }
        

        
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    private fun startPhoneNumberVerification() {
        //PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNumber.text.toString(),60,TimeUnit.SECONDS,,mCallBacks)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }
}