package com.example.livemap.objects;


import android.location.Location;
import android.util.Log;

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
    //this can be different from owner ID, for example when it is
    // owned by a group and created by a user, marker can be deleted both
    // by owner (e.g. group admin when in group) and by creator (e.g. regular user in group)
    private String creatorId;
    private boolean isPublic;
    private String id;
    private boolean visible;
    private String title;
    private String snippet;
    private LatLngLive position;
    private float rotation;
    private int numOfCopies;
    private BitmapDescriptor icon;
    //remove when uploading to DB and add when creating marker on device
    private transient Marker marker;


    // for Firebase serialization
    public MarkerLive(){}

    // standard constructor, constructor with MarkerOptions uses it
    public MarkerLive(MarkerOwner owner, String creatorId, LatLng latLng ,boolean isPublic) {
        this.isPublic = isPublic;
        this.creatorId = creatorId;
        this.owner = owner;
        this.position=new LatLngLive(latLng);
        this.id = UUID.randomUUID().toString();
        // this is the number of copies of this marker, if it's 1 then it's safe to remove from
        // database, otherwise instead of removing it only the number of copies will be reduced by 1
        this.numOfCopies = 1;
    }
    public MarkerLive(MarkerOwner owner, String creatorId,MarkerOptions markerOptions,boolean isPublic) {
        this(owner, creatorId,markerOptions.getPosition(), isPublic);
        this.isPublic = isPublic;
        this.title = markerOptions.getTitle();
        this.snippet = markerOptions.getSnippet();
        this.rotation = markerOptions.getRotation();
        this.icon = markerOptions.getIcon();
        this.owner=owner;
    }


    // attach marker to user or group after restoring from database
    public void restoreOwner(User user){
        //Log.w("MarkerLive", "restore owner: markerownerId is: "+ownerId+"user id is: "+user.getId());
        if(ownerId.equals(user.getId())){
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

    // removes itself from owners and deletes corresponding marker from map
    public void cleanup(){
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

    public int getNumOfCopies() {
        return numOfCopies;
    }

    public void setNumOfCopies(int numOfCopies) {
        this.numOfCopies = numOfCopies;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
