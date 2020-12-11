package com.example.livemap.objects;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerLive  {


    private boolean isPublic;
    private String ownerHash;
    private Marker marker;
    static int count = 0 ;

    public MarkerLive(){}

    public MarkerLive(String ownerHash , Marker marker ,boolean isPublic  ) {
        this.isPublic = isPublic;
        this.marker = marker;
        this.ownerHash = ownerHash;
        count++;
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


    public static int getCount() {
        return count;
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
