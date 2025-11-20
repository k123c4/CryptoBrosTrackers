package com.example.cryptobrostrackers.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WakeupAPI {
    @GET("wakeup")
    Call<JsonObject> wakeup();
}
