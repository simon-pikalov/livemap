package com.example.livemap.objects;

import java.util.HashMap;

/**
 * This class if hash map implementation of MapCollection
 *
 * @author Simon Pikalov
 */
public class MapDataSet implements MapCollection {
    private HashMap<String, MarkerLive> locations;

    public MapDataSet() {
        locations = new HashMap<>();
    }

    public MapDataSet(HashMap<String, MarkerLive> locations) {
        this.locations = locations;
    }

    @Override
    public void insert(MarkerLive markerLive) {
    locations.put(markerLive.getHash(),markerLive);
    }

    @Override
    public void remove(MarkerLive markerLive) {
        locations.remove(markerLive.getHash());
    }

    @Override
    public boolean contains(MarkerLive markerLive) {
        return locations.containsKey(markerLive.getHash());
    }


    public HashMap<String, MarkerLive> getLocations() {
        return locations;
    }

    public void setLocations(HashMap<String, MarkerLive> locations) {
        this.locations = locations;
    }


    @Override
    public String toString() {
        return "MapDataSet{" +
                "locations=" + locations +
                '}';
    }
}
