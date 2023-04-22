package com.example.colectivos_interurbanos_aguilar.retrofit;

import com.example.colectivos_interurbanos_aguilar.models.FCMBody;
import com.example.colectivos_interurbanos_aguilar.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAArbGsnEA:APA91bEM-jXPtrxLWPf8UQxI_QPsdNUDYZszC8RA4t8vu8yjMOIBE-U0WXWEdQlA1KlLSKTJr_fcGjdlasVzdz2OVVbIT-9re7XWOmGt9-RsnMu1jjnWKsVjNn5sCLWW5d9Yfl8OhhLx"
    })

    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);

}
