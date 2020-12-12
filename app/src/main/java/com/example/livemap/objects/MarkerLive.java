package com.example.livemap.objects;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * This class if container of google  Marker with some adjustment
 * @author Simon Pikalov
 */
public class MarkerLive  {


    private boolean isPublic;
    private String ownerHash;
    private String markerHash;
    private Marker marker;


    public MarkerLive(){}

    public MarkerLive(String ownerHash , Marker marker ,boolean isPublic  ) {
        this.isPublic = isPublic;
        this.marker = marker;
        this.ownerHash = ownerHash;
        this.markerHash = String.valueOf(marker.hashCode());

    }


    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }


    public String getOwnerHash() {
        return ownerHash;
    }

    public void setOwnerHash(String ownerHash) {
        this.ownerHash = ownerHash;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getMarkerHash() {
        return markerHash;
    }

    @Override
    public String toString() {
        return "MarkerLive{" +
                ", isPublic=" + isPublic +
                ", ownerHash='" + ownerHash + '\'' +
                ", marker=" + marker +
                '}';
    }
}
