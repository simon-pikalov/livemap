package com.example.livemap.objects;


import com.example.livemap.utils.LatLngLive;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

/**
 * This class if container of google  Marker with some adjustment
 * @author Simon Pikalov
 */
@IgnoreExtraProperties
public class MarkerLive  {


    private boolean isPublic;
    private String ownerHash;
    private String hash;
    private String title;
    private String snippet;
    private LatLngLive position;
    private float rotation;
    private BitmapDescriptor icon;
    //remove when uploading to DB and add when creating marker on device
    private transient Marker marker;


    public MarkerLive(){}

    public MarkerLive(String ownerHash, LatLng position,boolean isPublic) {
        this.isPublic = isPublic;
        this.position = new LatLngLive(position);
        this.ownerHash = ownerHash;
        this.hash = UUID.randomUUID().toString();
    }

    public MarkerLive(String ownerHash, MarkerOptions markerOptions,boolean isPublic) {
        this.isPublic = isPublic;
        this.position = new LatLngLive(markerOptions.getPosition());
        this.title = markerOptions.getTitle();
        this.snippet = markerOptions.getSnippet();
        this.ownerHash = ownerHash;
        this.hash = UUID.randomUUID().toString();
        this.rotation = markerOptions.getRotation();
        this.icon = markerOptions.getIcon();
    }

    public MarkerLive setTitle(String title){
        this.title=title;
        return this;
    }
    public MarkerLive setSnippet(String snippet){
        this.snippet = snippet;
        return this;
    }

    public MarkerLive attachMarker(Marker m){
        this.marker=m;
        m.setTag(this);
        return this;
    }

    public MarkerLive setPosition(LatLng pos){
        this.position = new LatLngLive(pos);
        return this;
    }
    public MarkerLive setIcon(BitmapDescriptor icon){
        this.icon = icon;
        return this;
    }

    @Exclude
    public Marker getMarker(){return marker;}
    public String getTitle(){ return title;}
    public String getSnippet(){return snippet;}
    public LatLngLive getPosition(){return position;}
    public boolean isPublic() {
        return isPublic;
    }
    public String getOwnerHash() {
        return ownerHash;
    }

    public LatLng getGoogleLatLng(){return position.getLatLng();}

    @Exclude
    public MarkerOptions getMarkerOptions(){
        MarkerOptions mo = new MarkerOptions();
        mo = mo.snippet(snippet).title(title).position(position.getLatLng()).icon(icon);
        return mo;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }




//    public void setOwnerHash(String ownerHash) {
//        this.ownerHash = ownerHash;
//    }



    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "MarkerLive{" +
                "isPublic=" + isPublic +
                ", ownerHash='" + ownerHash + '\'' +
                ", hash='" + hash + '\'' +
                ", Position=" + getPosition() +
                ", Title=" + getTitle() +
                ", Snippet=" + getSnippet() +
                '}';
    }
}
