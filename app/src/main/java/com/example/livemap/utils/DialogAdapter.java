package com.example.livemap.utils;


// Simple class to transfer data between dialog and caller
public class DialogAdapter {
    private boolean pressedAccept;
    private boolean pressedRemove;
    private String markerName;
    private String markerDescription;

    //public DialogAdapter(boolean pressedAccept, bo)

    public boolean hasPressedAccept(){return pressedAccept;}
    public boolean hasPressedRemove(){return pressedRemove;}

    public String getMarkerDescription() {return markerDescription; }

    public String getMarkerName() {return markerName; }

}
