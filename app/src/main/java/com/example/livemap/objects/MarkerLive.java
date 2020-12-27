package com.example.livemap.objects;


import com.google.android.gms.maps.model.MarkerOptions;

import java.util.UUID;

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

    public MarkerLive(String ownerHash , MarkerOptions mo ,boolean isPublic) {
        this.isPublic = isPublic;
        this.markerOptions = mo;
        this.ownerHash = ownerHash;
        this.markerHash = UUID.randomUUID().toString();
    }
    public void setTitle(String title){this.markerOptions= markerOptions.title(title);}
    public void setSnippet(String snippet){ this.markerOptions = markerOptions.snippet(snippet);}

    public String getTitle(){ return markerOptions.getTitle();}
    public String getSnippet(){return markerOptions.getSnippet();}

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
                "isPublic=" + isPublic +
                ", ownerHash='" + ownerHash + '\'' +
                ", markerHash='" + markerHash + '\'' +
                ", position=" + markerOptions.getPosition() +
                ", name=" + markerOptions.getTitle() +
                ", description=" + markerOptions.getSnippet() +
                '}';
    }
}
