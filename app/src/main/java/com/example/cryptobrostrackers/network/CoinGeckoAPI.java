package com.example.cryptobrostrackers.network;

import com.example.cryptobrostrackers.model.CoinMarket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface CoinGeckoAPI {

    Call<List<CoinMarket>> getMarkets(
            String vsCurrency,
            String order,
            int perPage,
            int page,
            boolean sparkline,
            String apiKey
    );
}
