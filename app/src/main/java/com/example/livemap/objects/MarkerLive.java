package com.example.livemap.objects;


import com.example.livemap.utils.LatLngLive;
import com.example.livemap.utils.MarkerOwner;
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

    private MarkerOwner owner;
    private String ownerId;
    private boolean isPublic;
    private String id;
    private boolean visible;
    private String title;
    private String snippet;
    private LatLngLive position;
    private float rotation;
    private BitmapDescriptor icon;
    //remove when uploading to DB and add when creating marker on device
    private transient Marker marker;


    public MarkerLive(){}

    public MarkerLive(MarkerOwner owner, LatLng latLng ,boolean isPublic) {
        this.isPublic = isPublic;
        this.owner = owner;
        this.position=new LatLngLive(latLng);
        this.id = UUID.randomUUID().toString();
    }
    public MarkerLive(MarkerOwner owner, MarkerOptions markerOptions,boolean isPublic) {
        this(owner, markerOptions.getPosition(), isPublic);
        this.isPublic = isPublic;
        this.title = markerOptions.getTitle();
        this.snippet = markerOptions.getSnippet();
        this.rotation = markerOptions.getRotation();
        this.icon = markerOptions.getIcon();
        this.owner=owner;
    }



    public void restoreOwner(User user){
        if(ownerId==user.getId()){
            owner = user;
        }
        else {
            Group group = user.getGroupById(ownerId);
            if (group != null) owner=group;
            else throw new RuntimeException("attempted to restore a marker without existing owner");
        }
    }

    public MarkerLive attachMarker(Marker m){
        this.marker=m;
        m.setTag(this);
        return this;
    }

    public void updateMarker(){
        if(marker==null){
            throw new NullPointerException("MarkerLive is not attached to a Marker");
        }
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setIcon(icon);
        marker.setPosition(position.getLatLng());
        marker.setRotation(rotation);
        marker.setVisible(visible);
    }


    public void cleanupMarker(){
        owner.removeMarkerLive(this);
        marker.remove();
    }


    public MarkerLive setTitle(String title){
        this.title=title;
        return this;
    }
    public MarkerLive setSnippet(String snippet){
        this.snippet = snippet;
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
    public boolean isVisible(){return visible;}

    public String getOwnerId() { return owner.getId(); }
    public void setOwnerId(String id) { this.ownerId=id; }

    @Exclude
    public LatLng getGoogleLatLng(){return position.getLatLng();}

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Exclude
    public MarkerOptions getMarkerOptions(){
        MarkerOptions mo = new MarkerOptions();
        mo = mo.snippet(snippet).title(title).position(position.getLatLng()).icon(icon);
        return mo;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getId() {
        return id;
    }

    @Exclude
    public MarkerOwner getOwner() {
        return owner;
    }

    public void setOwner(MarkerOwner owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "MarkerLive{id='" + id + '\'' +
                ", Position=" + getPosition() +
                ", Title=" + getTitle() +  '}';
    }
}
