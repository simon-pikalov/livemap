package com.example.livemap.utils;

import com.example.livemap.objects.MarkerLive;

public interface MarkerOwner {
    public String getId();
    public String getName();
    public void removeMarkerLive(MarkerLive ml);
    public void addMarkerLive(MarkerLive ml);

}
