package com.example.colectivos_interurbanos_aguilar.providers;

import com.example.colectivos_interurbanos_aguilar.models.FCMBody;
import com.example.colectivos_interurbanos_aguilar.models.FCMResponse;
import com.example.colectivos_interurbanos_aguilar.retrofit.IFCMApi;
import com.example.colectivos_interurbanos_aguilar.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body){

        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);

    }

}
