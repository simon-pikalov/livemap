package com.example.livemap.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

// this class' purpose is to allow serialization of LatLng in MarkerLive
public class LatLngLive {
    private double latitude;
    private double longitude;

    // for firebase
    LatLngLive(){}

    public LatLngLive(LatLng latLng){
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public LatLng getLatLng(){return new LatLng(latitude,longitude);}

    public String toString(){return "("+latitude+","+longitude+")";}
}
