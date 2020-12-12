package com.example.livemap.objects;


import com.google.android.gms.maps.model.MarkerOptions;

/**
 * This class if container of google  Marker with some adjustment
 * @author Simon Pikalov
 */
public class MarkerLive  {


    private boolean isPublic;
    private String ownerHash;
    private String markerHash;
    private MarkerOptions markerOptions;


    public MarkerLive(){}

    public MarkerLive(String ownerHash , MarkerOptions marker ,boolean isPublic  ) {
        this.isPublic = isPublic;
        this.markerOptions = marker;
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

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions) {
        this.markerOptions = markerOptions;
    }

    public String getMarkerHash() {
        return markerHash;
    }

    @Override
    public String toString() {
        return "MarkerLive{" +
                ", isPublic=" + isPublic +
                ", ownerHash='" + ownerHash + '\'' +
                ", marker=" + markerOptions +
                '}';
    }
}
