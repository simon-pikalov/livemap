package com.example.livemap.utils;

import com.google.gson.Gson;

public class GpsUtils {

    /**
     * function to calc tidtense between two points of gps
     * @param lat1 gps cord 1  lat
     * @param lon1 gps cord 1  lan
     * @param lat2 gps cord 2  lat
     * @param lon2 gps cord 1  lan
     * @return
     */
    public static double convertToMeters(double lat1, double lon1, double lat2,double lon2){  // generally used geo measurement function


        if(lat2==Double.MAX_VALUE) return -1;

        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return (d * 1000); // meters
    }


}
