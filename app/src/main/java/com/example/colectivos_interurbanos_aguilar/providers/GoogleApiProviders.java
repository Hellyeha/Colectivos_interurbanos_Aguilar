package com.example.colectivos_interurbanos_aguilar.providers;

import android.content.Context;

import com.example.colectivos_interurbanos_aguilar.R;
import com.example.colectivos_interurbanos_aguilar.retrofit.IGoogleApi;
import com.example.colectivos_interurbanos_aguilar.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProviders {

    private Context context;

    public GoogleApiProviders(Context context){
        this.context = context;
    }

    // query o peticion a google map para trazado de trayecto

    public Call<String> getDirections(LatLng originLatLng, LatLng destinationLatLng){
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                        + "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&"
                        + "destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&"
                        + "departure_time=" + (new Date().getTime() + (60+60+1000)) + "&"
                        + "traffic_model=best_guess&"
                        + "key=" + context.getResources().getString(R.string.google_maps_key);
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl + query);
    }

}
