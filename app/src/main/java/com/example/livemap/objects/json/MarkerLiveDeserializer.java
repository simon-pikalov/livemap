package com.example.livemap.objects.json;

import android.util.Log;

import com.example.livemap.objects.MapDataSet;
import com.example.livemap.objects.MarkerLive;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MarkerLiveDeserializer implements JsonDeserializer<MarkerLive> {
    @Override
    public MarkerLive deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Log.w("Firebase", "MarkerLiveDeserializer is: " + jsonObject);
        boolean isPublic = jsonObject.get("public").getAsBoolean(); //parse is public
        String ownerHash = jsonObject.get("ownerHash").getAsString(); //parse is public
        JsonObject jMarker = jsonObject.get("marker").getAsJsonObject(); //parse the google marker
        Gson gson = new Gson();
        MarkerOptions marker = gson.fromJson(jMarker,MarkerOptions.class);
        return new MarkerLive(ownerHash,marker,isPublic);
    }
}
