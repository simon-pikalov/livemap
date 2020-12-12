package com.example.livemap.objects.json;

import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.example.livemap.objects.MapDataSet;
import com.example.livemap.objects.MarkerLive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.json.JSONObject;

public class MapDataSetDeserializer implements JsonDeserializer<MapDataSet> {
    @Override
    public MapDataSet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        HashMap<String, MarkerLive> dMap = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();
        Log.w("Firebase", "jsonString2 is: " + jsonObject);

        GsonBuilder gsonBuilder = new GsonBuilder();
        MarkerLiveDeserializer markerLiveDeserializer = new MarkerLiveDeserializer();
        gsonBuilder.registerTypeAdapter(MarkerLive.class, markerLiveDeserializer);
        Gson gson = gsonBuilder.create();
        MarkerLive tempMarker;
        String key;
        for (Entry<String, JsonElement> set : jsonObject.entrySet()) {
             key = set.getKey();
            JsonElement jsonValueElement = set.getValue(); //the value of the hashmap as json element
            tempMarker = gson.fromJson(jsonValueElement, MarkerLive.class);
           dMap.put(key,tempMarker);
        }


        return new MapDataSet(dMap);
    }



}