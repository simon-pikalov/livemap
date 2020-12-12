package com.example.livemap.objects;


/**
 * This interface represent a Collection of Markerlive
 * @author Simon Pikalov
 */
public interface MapCollection {

    /**
     *
     * @param markerLive the marker to be added
     */
    void insert(MarkerLive markerLive);

    /**
     *
     * @param markerLive the marker to be removed
     */
    void remove(MarkerLive markerLive);


    /**
     *
     * @param markerLive the marker to check ig present
     * @return
     */
    boolean contains(MarkerLive markerLive);



}
