package com.example.cryptobrostrackers.network;

import com.example.cryptobrostrackers.model.CoinMarket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CoinGeckoAPI {

    // Pass the API key in the header param
    Call<List<CoinMarket>> getMarkets(
            @Query("vs_currency") String vsCurrency,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline,
            @Header("x-cg-api-key") String apiKey
    );
}
